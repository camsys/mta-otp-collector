package com.camsys.shims.gtfsrt.tripUpdates.lirr.service;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.LocalizedServiceId;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Listen for signage updates from enterprise queue and add to internal beans destined for GTFS-RT
 * Ported from org.opentripplanner.routing.graph.LIRRSolariDataService.
 * Please see integration test LIRRSolariDataServiceTest before making any changes!
 */
public class LIRRSolariDataService {

    private static Logger _log = LoggerFactory.getLogger(LIRRSolariDataService.class);

    private ExternalServices externalServices = new ExternalServicesBridgeFactory().getExternalServices();

    private String endpointUrl = null;

    private String username = null;

    private String password = null;

    private String topic = null;

    private Set<String> stationIdWhitelist = null;

    private GtfsDataService gtfsDataService = null;

    private ActiveMQConnectionFactory connectionFactory = null;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String _namespace;
    private Long now = null;
    private int tripWindowInMinutes = 90;
    private List<Trip> cachedActiveTrips = null;
    private long cachedActiveTripsTime = 0;
    private long cacheTimeMillis = 5 * 60 * 1000;
    private int tripCount = 0;
    private int matchCount = 0;
    private Map<String, AgencyAndId> stopCodeToStopIdMap = new HashMap<>();
    private Map<StopTimeUpdateKey, StopTimeUpdateAddon> updateCache = new PassiveExpiringMap<StopTimeUpdateKey, StopTimeUpdateAddon>(5*60*1000);

    private Connection connection = null;

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setStationIdWhitelist(Set<String> stationIdWhitelist) {
        this.stationIdWhitelist = stationIdWhitelist;
    }

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
       this.gtfsDataService = gtfsDataService;
    }

    public void setTripWindowInMinutes(int tripWindowInMinutes) {
        this.tripWindowInMinutes = tripWindowInMinutes;
    }

    public void setNamespace(String namespace) {
        this._namespace = namespace;
    }

    public int getTripCount() {
        return tripCount;
    }

    public int getMatchCount() {
        return matchCount;
    }
    public Map<StopTimeUpdateKey, StopTimeUpdateAddon> getUpdateCache() {
        return updateCache;
    }

    void resetActiveTrips() {
        cachedActiveTrips = null;
    }

    void setNow(long now) {
        this.now = now;
    }
    long getCurrentTime() {
        if (now == null)
            return System.currentTimeMillis();
        return now;
    }
    public LIRRSolariDataService() {

    }

    public boolean connect() {
        if (endpointUrl != null
                && username != null
                && password != null
                && topic != null
                && stationIdWhitelist != null
                && !stationIdWhitelist.isEmpty()) {
            connectionFactory = new ActiveMQConnectionFactory(username, password, endpointUrl);
        } else {
            _log.info("missing required configuration, exiting");
            return false;
        }

        connection = null;
        // don't let setup failure prevent startup
        try {
            _log.info("starting LIRR Solari initialization");
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);

            ProcessorThread thread = new ProcessorThread(destination, session);
            thread.setName("LIRR Solari Processor Thread");
            thread.start();
            _log.info("LIRR Solari initialization complete!");

        } catch (JMSException e) {
            _log.error("LIRR Solari initialization failed: {}", e, e);
        }
        return true;
    }

    void processMessage(JsonNode message) {
        JsonNode stationLocation = message.get("location");
        String stationCode = stationLocation.get("code").asText();
        // verify the age of the message -- this also checks against timezone issues
        long reportedTime = parseTrainDate(message.get("reportedDTM").asText());
        long now = getCurrentTime();
        if (Math.abs(reportedTime - now) > 5 * 60 * 1000) {
            _log.error("dropping old update with date {}", new Date(reportedTime));
            return;
        }
        // iterate over trains
        for (JsonNode train : message.get("trains")) {
            String trainNumberRaw = train.get("trainNumber").asText();
            String directionRaw = train.get("direction").asText();
            long predictionDate = 0;
            if (train.has("predictedDateTime")) {
                predictionDate = parseTrainDate(train.get("predictedDateTime").asText());
            }

            // this information is destined for GTFS-RT which has a narrow window of applicability
            // no need to consider data outside this window
            if (predictionDate + (this.tripWindowInMinutes * 60 * 1000) < now) {
                _log.debug("dropping historical trip {} with prediction {}", trainNumberRaw, new Date(predictionDate));
                continue;
            }

            if (predictionDate - (this.tripWindowInMinutes * 60 * 1000) > now) {
                _log.debug("dropping future trip {} with prediction {}:{}", trainNumberRaw, new Date(predictionDate), train.get("predictedDateTime"));
                continue;
            }

            // get a list of potential trips for this train + direction
            List<Trip> potentialTrips = getPotentialTripsForTrain(now, trainNumberRaw, directionRaw);
            if (potentialTrips.size() > 1) {
                // our matching algorithm is pretty trivial.  If there are duplicates we
                // simply drop the update
                _log.error("too many potential trains ({}}, skipping update for {}:{}",
                        potentialTrips.size(), trainNumberRaw, directionRaw);
                continue;
            }
            if (!potentialTrips.isEmpty()) {
                cacheTripDetails(stationCode, potentialTrips.get(0), train);
                // keep some stats on how well our matching performed
                logMatch();
            } else {
               _log.error("no match for train " + trainNumberRaw + " and prediction " + new Date(predictionDate));
            }
            // keep some stats on the data we've seen
            logTrip();
        }

    }

    // return the date in Solari packet as a millis since epoch.  Handle timezones as well.
    long parseTrainDate(String predictedDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parse = null;
        try {
            parse = sdf.parse(predictedDateTime);
        } catch (ParseException e) {
            _log.error("invalid date {}", predictedDateTime);
        }
        if (parse != null) {
            return parse.getTime();
        }
        return 0;
    }

    /**
     * trivial algorithm to match "active" trips to train and direction.
     * @param now
     * @param trainNumber
     * @param directionRaw
     * @return
     */
    List<Trip> getPotentialTripsForTrain(long now, String trainNumber, String directionRaw) {
        List<Trip> potentialTrips = new ArrayList<>();
        // consider only currently active trips
        for (Trip potentialTrip : getActiveTrips(now)) {
            // trivial matching of trip_id and trip direction to solari information
            if (tripMatchesTrain(potentialTrip, trainNumber, directionRaw)) {
                potentialTrips.add(potentialTrip);
            }
        }
        return potentialTrips;
    }

    // update expiring cache of internal beans for GTFS-RT consumption
    private void cacheTripDetails(String stationCode, Trip potentialTrip, JsonNode train) {
        AgencyAndId stopId = getStopIdForStopCode(stationCode);
        StopTimeUpdateAddon update = getTrainAsBean(potentialTrip, stopId, train);
        StopTimeUpdateKey key = new StopTimeUpdateKey(potentialTrip.getId(), stopId);
        updateCache.put(key, update);
    }

    private AgencyAndId getStopIdForStopCode(String stationCode) {
        if (stopCodeToStopIdMap.containsKey(stationCode))
            return stopCodeToStopIdMap.get(stationCode);
        for (Stop stop : gtfsDataService.getAllStops()) {
            if (stationCode.equals(stop.getCode())) {
                stopCodeToStopIdMap.put(stop.getCode(), stop.getId());
                return stop.getId();
            }
        }
        return null; // not found!
    }

    private StopTimeUpdateAddon getTrainAsBean(Trip trip, AgencyAndId stopId, JsonNode train) {
        StopTimeUpdateAddon update = new StopTimeUpdateAddon();
        update.setTripId(trip.getId());
        update.setStopId(stopId);
        update.setTripHeadsign(train.get("destinationLocation").get("name").asText());
        update.setTrack(train.get("track").asText(null)); // accept null as default
        update.setPeakCode(getOffPeakCode(train.get("peakCode").asText()));
        update.setStatus(train.get("status").asText());
        update.setScheduledDeparture(parseTrainDate(train.get("scheduleDateTime").asText()));
        update.setPredictedDeparture(parseTrainDate(train.get("predictedDateTime").asText()));
        return update;
    }

    // TODO: this keys/values in this method need to be verified
    private int getOffPeakCode(String s) {
        if (s == null) {
            _log.error("null peak code");
            return -1;
        }
        if ("O".equals(s))
            return 0;
        if ("P".equals(s))
            return 1;
        if ("A".equals(s)) {
            _log.info("A off peak code -- needs translated");
            return 2; // TODO!!!!!!!!!!
        }
        _log.error("unexpected peak code {}", s);
        return -2;
    }

    // match the trip_id and trip direction to solari packet
    // package private for unit tests
    boolean tripMatchesTrain(Trip trip, String trainNumber, String directionRaw) {
        String directionId = getDirectionIdFromNews(directionRaw);
        if (trip == null || trainNumber == null) return false;
        String[] tripParts = trip.getId().getId().split("_");
        if (tripParts.length < 3) {
            throw new IllegalStateException("unexpected trip format " + trip.getId().getId());
        }
        boolean tripIdMatches = trainNumber.equals(tripParts[2]);
        boolean directionMatches = directionId.equals(trip.getDirectionId());
        return tripIdMatches && directionMatches;
    }

    // translate compass direction to GTFS directionId
    private String getDirectionIdFromNews(String directionRaw) {
        switch (directionRaw) {
            case "E":
                return "0";
            case "W":
                return "1";
            default:
                throw new IllegalStateException("unknown direction " + directionRaw);
        }
    }

    List<Trip> getActiveTrips(long now) {
        if (now - cachedActiveTripsTime > cacheTimeMillis
                || cachedActiveTrips == null) {
            resetStats();
            List<Trip> activeTrips = new ArrayList<>();

            // handle yesterday's service ids if still active
            if (yesterdayInWindow(now)) {
                activeTrips.addAll(getActiveTripsNoCache(yesterdaysWindow(now)));
            }
            activeTrips.addAll(getActiveTripsNoCache(now));
            if (tommorrowInWindow(now)) {
                // handle tomorrow's service ids if they will be active
                activeTrips.addAll(getActiveTripsNoCache(tomorrowsWindow(now)));
            }
            cachedActiveTrips = activeTrips;
            cachedActiveTripsTime = getCurrentTime();
        }
        return cachedActiveTrips;
    }

    long tomorrowsWindow(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.add(Calendar.MINUTE, tripWindowInMinutes);
        return c.getTimeInMillis();
    }

    boolean tommorrowInWindow(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        int today = c.get(Calendar.DAY_OF_YEAR);
        c.add(Calendar.MINUTE, tripWindowInMinutes);
        return today != c.get(Calendar.DAY_OF_YEAR);
    }

    long yesterdaysWindow(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.add(Calendar.MINUTE, -tripWindowInMinutes);
        return c.getTimeInMillis();
    }

    boolean yesterdayInWindow(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        int today = c.get(Calendar.DAY_OF_YEAR);
        c.add(Calendar.MINUTE, -tripWindowInMinutes);
        return today != c.get(Calendar.DAY_OF_YEAR);
    }

    // filter trips to active serviceId and a configurable window
    // config minutes > trip start time > trip end time > config minutes
    // package private for unit tests
    List<Trip> getActiveTripsNoCache(long now) {
        List<Trip> activeTrips = new ArrayList<>();
        for (Trip tripForAgency : gtfsDataService.getAllTrips()) {
            if (isActiveServiceId(now, tripForAgency)) {
                // next is the trip window within our timespan
                if (isTripInTimeWindow(now, tripForAgency)) {
                    activeTrips.add(tripForAgency);
                }
            }
        }
        return activeTrips;
    }

    private void logTrip() {
        // TODO push to cloudwatch
        tripCount++;
    }

    private void logMatch() {
        // TODO push to cloudwatch
        matchCount++;
    }


    private void resetStats() {
        publishMetric("tripCount", tripCount);
        publishMetric("matchCount", matchCount);
        // reset stats
        _log.info("resetting stats (tripCount={}, matchCount={})", tripCount, matchCount);
        tripCount = 0;
        matchCount = 0;
    }

    // preWindow < trip start time < trip end time < postWindow
    // package private for unit tests
    boolean isTripInTimeWindow(long now, Trip trip) {
        if (trip == null) return false;
        List<StopTime> stopTimesForTrip = gtfsDataService.getStopTimesForTrip(trip);
        if (stopTimesForTrip == null || stopTimesForTrip.isEmpty()) return false;
        int tripStartTimeSecondsIntoDay = stopTimesForTrip.get(0).getArrivalTime();
        int tripEndTimeSecondsIntoDay = stopTimesForTrip.get(stopTimesForTrip.size()-1).getDepartureTime();
        long serviceDay = getServiceDay(now);
        long startTime = serviceDay + (tripStartTimeSecondsIntoDay * 1000);
        long endTime = serviceDay + (tripEndTimeSecondsIntoDay * 1000);
        long preWindow = now - (tripWindowInMinutes * 60 * 1000);
        long postWindow = now + (tripWindowInMinutes * 60 * 1000);
        // pre-window > startTime > endTime > post-window
        // compact logic for date intersection:
        // Math.max(start1.getTime(), start2.getTime()) < Math.min(end1.getTime(), end2.getTime());
        if (Math.max(preWindow, startTime) < Math.min(postWindow, endTime)) {
            return true;
        }
        return false;
    }

    // return epoch millis from start of day
    private long getServiceDay(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c.getTimeInMillis();
    }

    // test if the given trip is active at the specified time "now".  "now" will
    // be corrected if not on a service day boundary
    // package private for unit tests
    boolean isActiveServiceId(long now, Trip trip) {
        LocalizedServiceId serviceId = gtfsDataService.getLocalizedServiceIdForAgencyAndServiceId(trip.getServiceId().getAgencyId(), trip.getServiceId());
        long serviceDate = getServiceDay(now);
        return gtfsDataService.isLocalizedServiceIdActiveOnDate(serviceId, new Date(serviceDate));
    }

    // take the JSON string and return as object.
    // package private for unit tests
    JsonNode toJson(String json) throws IOException {
        return objectMapper.readValue(json, JsonNode.class);
    }


    /**
     * Background thread for receiving AND processing data.
     * Packets are infrequent enough (every 1-5 seconds) that
     * processing need not occur on a separate thread
     */
    private class ProcessorThread extends Thread {
        private MessageConsumer consumer = null;
        private Session session = null;
        private Destination destination = null;

        public ProcessorThread(Destination destination, Session session) throws JMSException {
            this.session = session; // hang onto session in case we need to re-connect
            this.consumer = session.createConsumer(destination);
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = consumer.receive();
                    if (message != null) {
                        String rawJson = ((TextMessage) message).getText();
                        JsonNode jsonMessage = toJson(rawJson);
                        JsonNode stationLocation = jsonMessage.get("location");
                        String stationCode = stationLocation.get("code").asText();
                        // we are only interested in certain stops, ignore if it's not one of those
                        if (!stationIdWhitelist.contains(stationCode))
                            continue; // discard

                        processMessage(jsonMessage);

                    }
                } catch (javax.jms.IllegalStateException ise) {
                    _log.error("received exception from consumer, assuming connection is dead and reconnecting");
                    connect();
                    try {
                        consumer = session.createConsumer(destination);
                    } catch (JMSException e) {
                        _log.error("reconnect failed: {}", e, e);
                    }
                } catch (JMSException e) {
                    _log.error("exception communicating with Solari {}", e, e);
                } catch (Exception e) {
                    _log.error("issue parsing Solari packet {}", e, e);
                }
            }
        }
    }

    public void publishMetric(String name, double value) {
        if (externalServices.isInstancePrimary()) {
            externalServices.publishMetric(_namespace, name, "feed", "LI-SOLARI", value);
        }
    }
}
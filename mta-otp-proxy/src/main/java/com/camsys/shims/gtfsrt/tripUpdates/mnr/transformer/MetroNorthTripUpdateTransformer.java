/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.gtfsrt.tripUpdates.mnr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import com.google.transit.realtime.GtfsRealtimeMTARR;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.google.transit.realtime.GtfsRealtimeNYCT.NyctStopTimeUpdate;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetroNorthTripUpdateTransformer extends TripUpdateTransformer {

	// MMDDYYYY used by MNR trips
    private static final Pattern _sdPattern = Pattern.compile("^(\\d{2})(\\d{2})(\\d{4})$");

    // YYYYMMDD used by NJT trips
    private static final Pattern _njtSdPattern = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");

    private GtfsDataService _gtfsDataService;

    private String _agencyId = "MNR";

    private boolean _startDateIsServiceDate = true;

    private static final Logger _log = LoggerFactory.getLogger(MetroNorthTripUpdateTransformer.class);

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
        _gtfsDataService = gtfsDataService;
    }

    public void setAgencyId(String agencyId) {
        _agencyId = agencyId;
    }

    public void setStartDateIsServiceDate(boolean startDateIsServiceDate) {
        _startDateIsServiceDate = startDateIsServiceDate;
    }

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe) {
        TripUpdate tu = fe.getTripUpdate();
        TripUpdate.Builder tub = TripUpdate.newBuilder();
        tub.getTripBuilder().setScheduleRelationship(tu.getTrip().getScheduleRelationship());
        String routeId = null, tripShortName = null, startDate = null;
        if (tu.hasTrip() && tu.getTrip().hasRouteId()) {
            routeId = tu.getTrip().getRouteId();
        }
        if (tu.hasTrip() && tu.getTrip().hasStartDate()) {
            startDate = tu.getTrip().getStartDate();
        }
        if (fe.hasVehicle() && fe.getVehicle().hasVehicle() && fe.getVehicle().getVehicle().hasLabel()) {
            tripShortName = fe.getVehicle().getVehicle().getLabel();
        }

        // tripShortName can come from either FeedEntity (old convention) or TripUpdate (new convention)
        if (tripShortName == null && tu.hasVehicle() &&  tu.getVehicle().hasLabel()) {
            tripShortName = tu.getVehicle().getLabel();
        }
        if (routeId == null || startDate == null) {
            _log.info("not enough info for tripUpdate routeId={} tripShortName={} startDate={}", routeId, tripShortName, startDate);
            return null;
        }
        
        ServiceDate sd = parseDate(startDate); 
        if(sd == null) {
            _log.error("invalid date={}", startDate);
            return null;
        }
        
        Route route = _gtfsDataService.getRouteForId(new AgencyAndId(_agencyId, routeId));
        if (route == null || sd == null) {
            return null;
        }
        
        ActivatedTrip activatedTrip = findCorrectTrip(route, tripShortName, tu.getTrip().getDirectionId() + "", sd, tu.getTrip().getStartTime(), fe.getId().startsWith("COPIED-FROM-NJT-"));

        Set<String> stopIds = null;
        if (activatedTrip == null) {
            tub.getTripBuilder().setTripId(tripShortName + "_" + sd.getAsString());
            tub.getTripBuilder().setScheduleRelationship(ScheduleRelationship.ADDED);
            stopIds = _gtfsDataService.getAllStops().stream().map(s -> s.getId().getId()).collect(Collectors.toSet());
        } else {
            Trip trip = activatedTrip.trip;
            sd = activatedTrip.date;
            tub.getTripBuilder().setTripId(trip.getId().getId());
            stopIds = _gtfsDataService.getStopTimesForTrip(trip).stream()
                    .map(st -> st.getStop().getId().getId()).collect(Collectors.toSet());
        }

        tub.getTripBuilder().setStartDate(formatDate(sd));
        tub.getTripBuilder().setRouteId(routeId);

        int delay = 0;

        for (StopTimeUpdate stu : tu.getStopTimeUpdateList()) {
            if (!stu.getStopId().isEmpty() 
            		&& !stopIds.contains(stu.getStopId()) 
            		&& !stopIds.contains("NJT-" + stu.getStopId())) {
                continue;
            }
            
            StopTimeUpdate.Builder stub = stu.toBuilder();
            GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate ext = stub.getExtension(GtfsRealtimeMTARR.mtaRailroadStopTimeUpdate);
            if (ext.hasTrack()) {
                NyctStopTimeUpdate.Builder nyctExt = NyctStopTimeUpdate.newBuilder();
                nyctExt.setActualTrack(ext.getTrack());
                stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
            }
            
            if (stub.hasDeparture() && !stub.hasArrival()) {
                stub.setArrival(stub.getDeparture());
            }
            
            tub.addStopTimeUpdate(stub);
            
            if (stub.hasDeparture() && stub.getDeparture().hasDelay())
                delay = stub.getDeparture().getDelay();
        }

        tub.setDelay(delay);

        return tub;
    }

    // Capture metrics for scheduled trips with realtime. Could push this upstream for all transformers that have a GtfsDataService
    @Override
    public void publishMetrics(GtfsRealtime.FeedMessageOrBuilder messageIn, GtfsRealtime.FeedMessageOrBuilder messageOut, List<FeedEntity> unmatchedEntities) {
        super.publishMetrics(messageIn, messageOut, unmatchedEntities);


        Multimap<ServiceDate, String> rtTripIdsByServiceDate = ArrayListMultimap.create();
        try {
            for (FeedEntity entity : messageOut.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    TripUpdate tripUpdate = entity.getTripUpdate();
                    ServiceDate sd = ServiceDate.parseString(tripUpdate.getTrip().getStartDate());
                    rtTripIdsByServiceDate.put(sd, tripUpdate.getTrip().getTripId());
                }
            }
        } catch(ParseException ex) {
            _log.error("Unable to publish MNR metrics due to error {}", ex);
            return;
        }

        Date timestamp = new Date();
        Set<ActivatedTrip> tripsInService = getTripsInService(timestamp);
        int nService = tripsInService.size();
        int nRtInService = 0;
        for (ActivatedTrip trip : tripsInService) {
            if (rtTripIdsByServiceDate.get(trip.date).contains(trip.trip.getId().getId())) {
                nRtInService++;
            }
        }

        long totalRtTrips = messageOut.getEntityList()
                .stream().filter(FeedEntity::hasTripUpdate).count();
        publishMetric("TripsInService", nService);
        publishMetric("TripsInServiceRealtime", nRtInService);
        if (nService != 0) publishMetric("TripsInServiceRtPct", (double) nRtInService / (double) nService);
        publishMetric("ExtraRtTrips", totalRtTrips - nRtInService);
    }

    private Set<ActivatedTrip> getTripsInService(Date date) {
        Set<ActivatedTrip> activatedTrips = new HashSet<>();
        long now = date.getTime();
        ServiceDate today = new ServiceDate(date);
        for (ServiceDate sd : Arrays.asList(today.previous(), today, today.next())) {
            Set<AgencyAndId> serviceIds = _gtfsDataService.getServiceIdsOnDate(sd);
            for (AgencyAndId serviceId : serviceIds) {
                List<Trip> trips = _gtfsDataService.getTripsForServiceId(serviceId);
                for (Trip trip : trips) {
                    List<StopTime> stopTimes = _gtfsDataService.getStopTimesForTrip(trip);
                    long startTime = sd.getAsDate().getTime() + (stopTimes.get(0).getDepartureTime() * 1000);
                    long endTime = sd.getAsDate().getTime() + (stopTimes.get(stopTimes.size() - 1).getArrivalTime() * 1000);

                    // For MNR, we expect trips to be in the RT feed if they start up to 6 hours in the future, or if they
                    // start in the past and haven't reached their destination yet

                    boolean tracks_startOk = (startTime >= now) && (startTime <= now + 6 * 3600 * 1000);
                    int endBuffer = 300 * 1000;
                    boolean endInFuture = startTime <= now && (endTime + endBuffer) >= now;

                    if (tracks_startOk || endInFuture) {
                        activatedTrips.add(new ActivatedTrip(trip, sd));
                    }
                }
            }
        }
        return activatedTrips;
    }

    private ActivatedTrip findCorrectTrip(Route route, String tripShortName, String directionId, ServiceDate date, String startTime, boolean isNjtCopied) {
        if (date == null)
           return null;
        
        List<ActivatedTrip> candidates = new ArrayList<>();
        if (_startDateIsServiceDate) {
            Set<AgencyAndId> serviceIds = _gtfsDataService.getServiceIdsOnDate(date);
            for (Trip t : _gtfsDataService.getTripsForRoute(route)) {
                if (serviceIds.contains(t.getServiceId()) && t.getTripShortName().equals(tripShortName)) {
                    candidates.add(new ActivatedTrip(t, date));
                }
            }
        } else {
            // start date is calendar date. Check ServiceIds for yesterday's service date as well.
            Set<AgencyAndId> serviceIdsToday = _gtfsDataService.getServiceIdsOnDate(date);
            Set<AgencyAndId> serviceIdsYesterday = _gtfsDataService.getServiceIdsOnDate(date.previous());
            for (Trip t : _gtfsDataService.getTripsForRoute(route)) {
                if ((t.getTripShortName() != null && !t.getTripShortName().isEmpty() && t.getTripShortName().equals(tripShortName)) ||
                	((t.getTripShortName() == null || t.getTripShortName().isEmpty()) && t.getDirectionId() != null && t.getDirectionId().equals(directionId) && isNjtCopied)) {

                	boolean afterMidnight = tripStartsAfterMidnight(t);
                    if (!afterMidnight && serviceIdsToday.contains(t.getServiceId())) {
                        candidates.add(new ActivatedTrip(t, date));
                    } else if (afterMidnight && serviceIdsYesterday.contains(t.getServiceId())) {
                        candidates.add(new ActivatedTrip(t, date.previous()));
                    }
                }
            }
        }

        // if we have more than one trip matching in the GTFS, filter by time
        if(candidates.size() > 1 && startTime != null) {
        	// NJT format of "HH:MM:SS"
        	String[] timeParts = startTime.split(":");
        	if(timeParts.length != 3 && startTime.length() == 4) {        
        		// MNR format of HHMM
        		timeParts = new String[3];
        		timeParts[0] = startTime.substring(0,2);
        		timeParts[1] = startTime.substring(2,4);
        		timeParts[2] = "0";
        	} else
        		return null;
        		        		
       		int startTimeAsOffsetFromMidnight = (Integer.parseInt(timeParts[0]) * 3600) + (Integer.parseInt(timeParts[1]) * 60) + Integer.parseInt(timeParts[2]);

        	int minimumDifference = Integer.MAX_VALUE;
        	ActivatedTrip winningTrip = null;
        	
        	for(ActivatedTrip at : candidates) {
        		List<StopTime> stopTimes = _gtfsDataService.getStopTimesForTrip(at.trip);
        		StopTime start = stopTimes.get(0);

        		int difference = Math.abs(startTimeAsOffsetFromMidnight - start.getDepartureTime());

        		if(difference < minimumDifference) {        			
        			winningTrip = at;
        			minimumDifference = difference;
        		}
        	}        	

        	// winner needs to be within 15m of schedule, otherwise revert to prior logic of issuing
        	// a GTFS-RT ADDED message
        	if(winningTrip != null && minimumDifference < 15 * 60) {
        		if(minimumDifference > 0)
        			_log.warn("trip won with time diff of {}", minimumDifference);
        		candidates = List.of(winningTrip);
        	}
        }
        
        if (candidates.size() == 1)
            return candidates.get(0);
        else if (candidates.size() == 0)
            _log.error("not enough trips for shortName {}", tripShortName);
        else
            _log.error("Too many trips for shortName {} ({})", tripShortName, candidates);
        return null;
    }

    private boolean tripStartsAfterMidnight(Trip trip) {
        int startTime = _gtfsDataService.getStopTimesForTrip(trip).get(0).getDepartureTime();
        return startTime > (24 * 3600);
    }
    
    private ServiceDate parseDate(String date) {
    	Matcher matcher = _sdPattern.matcher(date);

    	if(matcher.matches()) {
            int year = Integer.parseInt(matcher.group(3));
            int month = Integer.parseInt(matcher.group(1));
            int day = Integer.parseInt(matcher.group(2));
       
            // must be an NJT format? 
            if(year < 2000 || month > 12) {            
            	matcher = _njtSdPattern.matcher(date);
                if(!matcher.matches()) {
                    return null;
                } else {
                    year = Integer.parseInt(matcher.group(1));
                    month = Integer.parseInt(matcher.group(2));
                    day = Integer.parseInt(matcher.group(3));
                    
                    if(year < 2000 || month > 12)
                    	return null;                    
                }
            }
            
            return new ServiceDate(year, month, day);
        }
    	
    	return null;
    }

    private String formatDate(ServiceDate sd) {
        return String.format("%04d%02d%02d", sd.getYear(), sd.getMonth(), sd.getDay());
    }

    private class ActivatedTrip {
        Trip trip;
        ServiceDate date;
        ActivatedTrip(Trip trip, ServiceDate date) {
            this.trip = trip;
            this.date = date;
        }
    }
}

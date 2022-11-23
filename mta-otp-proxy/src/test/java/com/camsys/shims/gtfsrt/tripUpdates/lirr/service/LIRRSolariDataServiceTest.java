package com.camsys.shims.gtfsrt.tripUpdates.lirr.service;

import com.camsys.shims.factory.GtfsDataServiceFactory;
import com.camsys.shims.util.gtfs_provider.GtfsDataServiceProvider;
import com.camsys.shims.util.gtfs_provider.GtfsDataServiceProviderImpl;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for connecting to signage enterprise queue.
 * Solari messages look like this:
 {
    "sourceSystemCode": "AVPS",
    "location": {
        "id": 15015,
        "name": "Merrick",
        "code": "MRK",
        "poiNum": 226,
        "latitude": 40.663800403938,
        "longitude": -73.55062102286
    },
    "reportedDTM": "2022-11-16T12:51:48Z",
    "systemDTM": "2022-11-16T12:51:48Z",
    "trains": [
        {
            "trainNumber": "39",
            "trainVersion": "0",
            "runDate": "2022-11-16",
            "direction": "W",
            "destinationLocation": {
                "id": 15027,
                "name": "Atlantic Terminal",
                "code": "ATL",
                "poiNum": 241,
                "latitude": 40.683595959933,
                "longitude": -73.975671116775
            },
           "scheduleDateTime": "2022-11-16T12:56:00Z",
            "predictedDateTime": "2022-11-16T13:00:50Z",
            "originalTrackPlatform": "1",
            "track": "1",
            "platform": "",
            "originalStatus": "On Time",
            "status": "On Time",
            "remark": null,
            "stops": [
                {
                    "id": 14860,
                    "name": "Freeport",
                    "code": "FPT",
                    "poiNum": 64,
                    "latitude": 40.657457992806,
                    "longitude": -73.582324012042
                }
                ...
            ],
            "boardingAtTerminal": null,
            "connections": []
        },
        ...
    ]
}
 */
public class LIRRSolariDataServiceTest {
    private static Logger _log = LoggerFactory.getLogger(LIRRSolariDataServiceTest.class);

    private LIRRSolariDataService service;

    private GtfsDataServiceProvider provider;
    private GtfsDataServiceFactory lirrGtfsServiceFactory;
    private GtfsDataService lirrGtfsService;
    private boolean integrationTestEnabled = false;

    @Before
    public void setup() throws Exception {
        // we only need to set up once, it's expensive
        if (service == null) {
            service = new LIRRSolariDataService();
            String username = System.getProperty("solari.username");
            String password = System.getProperty("solari.password");
            String endpoint = System.getProperty("solari.endpoint");
            String topic = System.getProperty("solari.topic");
            if (username != null && password != null
                    && endpoint != null && topic != null) {
                service.setUsername(username);
                service.setPassword(password);
                service.setEndpointUrl(endpoint);
                service.setTopic(topic);
                integrationTestEnabled = true;
            }
            service.setTripWindowInMinutes(30);
            Set<String> stations = new HashSet<>();
            stations.add("ATL"); // Atlantic
            stations.add("NYK"); // Penn Station
            stations.add("JAM"); // Jamaica
             stations.add("GCT"); // Grand Central Madison
            service.setStationIdWhitelist(stations);

            provider = new GtfsDataServiceProviderImpl();
            lirrGtfsServiceFactory = new GtfsDataServiceFactory();
            lirrGtfsServiceFactory.setProvider(provider);
            lirrGtfsServiceFactory.setGtfsPath("classpath:/lirr_gtfs.zip");
            lirrGtfsService = lirrGtfsServiceFactory.getObject();
            service.setGtfsDataService(lirrGtfsService);
        }
    }

    // this is an integration test that connects to remote queue
    // if configured
    @Test
    public void testConnect() throws Exception {
        if (!integrationTestEnabled) {
            _log.info("integration test not configured, exiting");
            return;
        }
        service.setTripWindowInMinutes(90);
        boolean success = service.connect();
        assertTrue(success);

        int loopCount = 0;
        while (service.getTripCount() < 100 && loopCount < 20) {
            _log.info(" waiting[" + loopCount + "] on trips with current count at " + service.getTripCount());
            Thread.currentThread().sleep(10 * 1000);
            loopCount++;
        }
        _log.info("trip Count: " + service.getTripCount());
        _log.info("match Count: " + service.getMatchCount());
    }

    @Test
    public void testProcessMessageATL() throws Exception {
        String solariJson = getClasspathFileAsString("/solari-ATL.json");
        service.setNow(service.parseTrainDate("2022-11-21T16:30:06Z"));
        service.setTripWindowInMinutes(90);
        service.processMessage(service.toJson(solariJson));
        assertEquals(3, service.getTripCount());
        assertEquals(3, service.getMatchCount());
        assertNotNull(service.getUpdateCache());
        assertEquals(3, service.getUpdateCache().size());
        StopTimeUpdateKey key = new StopTimeUpdateKey(new AgencyAndId("LI", "GO101A_22_2820"),
                new AgencyAndId("LI", "12")); // ATL->12
        StopTimeUpdateAddon value = service.getUpdateCache().get(key);
        assertNotNull(value);
        assertEquals(key.getTripId(), value.getTripId());
        assertEquals(key.getStopId(), value.getStopId());
        assertEquals("Far Rockaway", value.getTripHeadsign());
        assertEquals("3", value.getTrack());
        assertEquals(0, value.getPeakCode());
        assertEquals("ON TIME", value.getStatus());
        assertEquals(1669048920000l, value.getScheduledDeparture());
        assertEquals(1669048920000l, value.getPredictedDeparture());
    }

    @Test
    public void testProcessMessageNYK() throws Exception {
        String solariJson = getClasspathFileAsString("/solari-NYK.json");
        service.setNow(service.parseTrainDate("2022-11-21T16:30:49Z"));
        service.setTripWindowInMinutes(90);
        service.processMessage(service.toJson(solariJson));
        assertEquals(10, service.getTripCount());
        assertEquals(10, service.getMatchCount());
        assertNotNull(service.getUpdateCache());
        assertEquals(10, service.getUpdateCache().size());
        StopTimeUpdateKey key = new StopTimeUpdateKey(new AgencyAndId("LI", "GO101A_22_426_METS"),
                new AgencyAndId("LI", "8")); // NYK->8
        StopTimeUpdateAddon value = service.getUpdateCache().get(key);
        assertNotNull(value);
        assertEquals(key.getTripId(), value.getTripId());
        assertEquals(key.getStopId(), value.getStopId());
        assertEquals("Port Washington", value.getTripHeadsign());
        assertNull(value.getTrack());
        assertEquals(0, value.getPeakCode());
        assertEquals("ON TIME", value.getStatus());
        assertEquals(1669049340000l, value.getScheduledDeparture());
        assertEquals(1669049340000l, value.getPredictedDeparture());

        key = new StopTimeUpdateKey(new AgencyAndId("LI", "GO101A_22_1622"),
                new AgencyAndId("LI", "8")); // NYK->8
        value = service.getUpdateCache().get(key);
        assertNotNull(value);
        assertEquals(key.getTripId(), value.getTripId());
        assertEquals(key.getStopId(), value.getStopId());
        assertEquals("Huntington", value.getTripHeadsign());
        assertEquals("20", value.getTrack());
        assertEquals(0, value.getPeakCode());
        assertEquals("ON TIME", value.getStatus());
        assertEquals(1669048920000l, value.getScheduledDeparture());
        assertEquals(1669048920000l, value.getPredictedDeparture());

    }

    @Test
    public void getActiveTrips() throws ParseException {
        // GO101A_22_1001:89EF2111 20221114 05:59 -> 06:55
        Trip testTrip = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_1001"));
        assertNotNull(testTrip);
        // previous calendar
        long now = getMillis("20221113055900");
        List<Trip> activeTrips = service.getActiveTrips(now);
        assertFalse(activeTrips.contains(testTrip));
        service.resetActiveTrips();
        // active calendar
        now = getMillis("20221114055900");
        activeTrips = service.getActiveTrips(now);
        assertTrue(activeTrips.contains(testTrip));
        service.resetActiveTrips();
        // next calendar
        now = getMillis("20221119055900");
        activeTrips = service.getActiveTrips(now);
        assertFalse(activeTrips.contains(testTrip));
        service.resetActiveTrips();

        // trip that starts shortly after midnight
        // but checked for before midnight
        Trip tripAfterMidnight = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_1304"));
        assertNotNull(tripAfterMidnight);
        service.setTripWindowInMinutes(45);
        //GO101A_22_1304,8,00:35:00,00:35:00,,,,,1 89EF2111 (20221114) direction=0 (E)
        now = getMillis("20221113235900");
        activeTrips = service.getActiveTrips(now);
        // search should wrap around to next day
        assertTrue(activeTrips.contains(tripAfterMidnight));
        service.resetActiveTrips();
        now = getMillis("20221114003500");
        activeTrips = service.getActiveTrips(now);
        // confirm same day still works
        assertTrue(activeTrips.contains(tripAfterMidnight));
        service.resetActiveTrips();
        now = getMillis("20221114020000");
        activeTrips = service.getActiveTrips(now);
        // expired
        assertFalse(activeTrips.contains(tripAfterMidnight));
        service.resetActiveTrips();

        // trip that started just before midnight
        // but checked for after midnight
        //GO101A_22_972,87,23:21:00,23:21:00,,,,,1, -> 89EF2111 (20221114) direction=0 (E)
        now = getMillis("20221114235900");
        Trip tripBeforeMidnight = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_972"));
        activeTrips = service.getActiveTrips(now);
        // same day, confirm still works
        assertTrue(activeTrips.contains(tripBeforeMidnight));
        service.resetActiveTrips();
        now = getMillis("20221115000100");
        activeTrips = service.getActiveTrips(now);
        // search window needs to consider previous day
        assertTrue(activeTrips.contains(tripBeforeMidnight));
        service.resetActiveTrips();
        now = getMillis("20221115020100");
        activeTrips = service.getActiveTrips(now);
        // expired
        assertFalse(activeTrips.contains(tripBeforeMidnight));
    }

    @Test
    public void isInWindow() throws ParseException {
        // GO101A_22_1001:89EF2111 20221114 05:59 -> 06:55
        Trip testTrip = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_1001"));
        assertNotNull(testTrip);
        long now = getMillis("20221114052900"); // exactly on window
        assertFalse(service.isTripInTimeWindow(now, testTrip));

        now = getMillis("20221114052901"); // first second in window
        assertTrue(service.isTripInTimeWindow(now, testTrip));

        now = getMillis("20221114055900"); // trip start time
        assertTrue(service.isTripInTimeWindow(now, testTrip));

        now = getMillis("20221114065500"); // trip end time
        assertTrue(service.isTripInTimeWindow(now, testTrip));

        now = getMillis("20221114072459"); // last second in window
        assertTrue(service.isTripInTimeWindow(now, testTrip));

        now = getMillis("20221114072500"); // exactly on window
        assertFalse(service.isTripInTimeWindow(now, testTrip));

    }

    @Test
    public void isActiveServiceId() throws ParseException {
        // GO101A_22_1001:89EF2111 20221114 05:59 -> 06:55
        Trip testTrip = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_1001"));
        assertNotNull(testTrip);

        long now = getMillis("20221113055900"); // day before active
        assertFalse(service.isActiveServiceId(now, testTrip));

        now = getMillis("20221114055900"); // active
        assertTrue(service.isActiveServiceId(now, testTrip));

        now = getMillis("20221119055900"); // day after active
        assertFalse(service.isActiveServiceId(now, testTrip));
    }

    @Test
    public void tripMatchesTrain() {
        Trip testTrip = lirrGtfsService.getTripForId(new AgencyAndId("LI", "GO101A_22_1001"));
        assertTrue(service.tripMatchesTrain(testTrip, "1001", "W"));
        assertFalse(service.tripMatchesTrain(testTrip, "1001", "E"));

        assertFalse(service.tripMatchesTrain(testTrip, "1000", "W"));
        assertFalse(service.tripMatchesTrain(testTrip, "1000", "E"));
        assertFalse(service.tripMatchesTrain(testTrip, "1002", "W"));
        assertFalse(service.tripMatchesTrain(testTrip, "1002", "E"));
    }

    @Test
    public void getPotentialTripsForTrain() throws ParseException {
        long now = getMillis("20221114055900"); // trip start time
        List<Trip> potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "W");
        if (potentialTripsForTrain.size() > 1) {
            _log.info("expected one match, got "
                    + potentialTripsForTrain.size()
                    + ", " + potentialTripsForTrain);
        }

        assertEquals(1, potentialTripsForTrain.size()); // we really want just one train back

        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "E"); // wrong direction
        assertEquals(0, potentialTripsForTrain.size());
        service.resetActiveTrips();;
        now = getMillis("20221114065500"); // trip end time
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "W");
        assertEquals(1, potentialTripsForTrain.size()); // we really want just one train back
        service.resetActiveTrips();;
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "E"); // wrong direction
        assertEquals(0, potentialTripsForTrain.size());
        service.resetActiveTrips();
        now = getMillis("20221114072500"); // outside window
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "W");
        assertEquals(0, potentialTripsForTrain.size());

        service.setTripWindowInMinutes(40);
        service.resetActiveTrips();

        // trip that starts shortly after midnight
        // but checked for before midnight
        //GO101A_22_1304,8,00:35:00,00:35:00,,,,,1 89EF2111 (20221114) direction=0 (E)
        now = getMillis("20221113235900");
        service.resetActiveTrips();
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1304", "E");
        assertEquals(1, potentialTripsForTrain.size());

        // trip that started just before midnight
        // but checked for after midnight
        //GO101A_22_972,87,23:21:00,23:21:00,,,,,1, -> 89EF2111 (20221114) direction=0 (E)
        now = getMillis("20221115000100");
        service.resetActiveTrips();
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "972", "E");
        assertEquals(1, potentialTripsForTrain.size());

    }

    @Test
    public void testYesterdayInWindow() throws ParseException {
        service.setTripWindowInMinutes(30);
        long now = getMillis("20221113235959");
        assertFalse(service.yesterdayInWindow(now));

        now = getMillis("20221114000000");
        assertTrue(service.yesterdayInWindow(now));

        now = getMillis("20221114002959");
        assertTrue(service.yesterdayInWindow(now));

        now = getMillis("20221114003000");
        assertFalse(service.yesterdayInWindow(now));

    }

    @Test
    public void testTomorrowInWindow() throws ParseException {
        service.setTripWindowInMinutes(30);
        long now = getMillis("20221113232959");
        assertFalse(service.tommorrowInWindow(now));

        now = getMillis("20221113233000");
        assertTrue(service.tommorrowInWindow(now));

        now = getMillis("20221113235959");
        assertTrue(service.tommorrowInWindow(now));

        now = getMillis("20221114000000");
        assertFalse(service.tommorrowInWindow(now));

    }

    // convenience method to load a file on classpath into String
    private String getClasspathFileAsString(String pathRelativeToClasspath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(pathRelativeToClasspath);
        InputStream inputStream = classPathResource.getInputStream();
        if (inputStream == null) throw new RuntimeException("resource not found: " + pathRelativeToClasspath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inputStream.transferTo(baos);
        return baos.toString();
    }

    private long getMillis(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.parse(s).getTime();
    }

}
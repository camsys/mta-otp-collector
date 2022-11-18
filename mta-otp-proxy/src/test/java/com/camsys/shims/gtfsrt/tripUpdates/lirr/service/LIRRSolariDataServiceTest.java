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
            // TODO -- need station code for Grand Central Madison
            // stations.add(""); // Grand Central Madison
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
    public void testProcessMessage() throws Exception {
        String solariJson = getClasspathFileAsString("/solari.json");
        service.setNow(service.parseTrainDate("2022-11-17T18:14:20Z"));
        service.setTripWindowInMinutes(90);
        service.processMessage(service.toJson(solariJson));
        assertEquals(9, service.getTripCount());
        assertEquals(9, service.getMatchCount());
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
        service.resetActiveTrips();;
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
        service.resetActiveTrips();;
        now = getMillis("20221114072500"); // outside window
        potentialTripsForTrain = service.getPotentialTripsForTrain(now, "1001", "W");
        assertEquals(0, potentialTripsForTrain.size());
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
package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.model.ExtendedRouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpRequestStaticRouteInfoTest {
    /**
     * Test to make sure that ExtendedRouteBranchStop sorting does not break if locationIndex is missing
     */
    @Test(expected = Test.None.class /* no exception expected */)
    public void sortLIStopsTest(){
        HttpRequestStaticRouteInfo httpSRI = new HttpRequestStaticRouteInfo();

        RouteBranchStop rbs1 = new RouteBranchStop("LI:8", "Penn Station","West Hempstead","LI:8");
        ExtendedRouteBranchStop stop1 = new ExtendedRouteBranchStop(rbs1);
        stop1.setLat(40.750588);
        stop1.setLon(-73.993584);

        RouteBranchStop rbs2 = new RouteBranchStop("LI:9", "Woodside","West Hempstead","LI:8");
        ExtendedRouteBranchStop stop2 = new ExtendedRouteBranchStop(rbs2);
        stop2.setLat(40.745851);
        stop2.setLon(-73.902975);

        List<ExtendedRouteBranchStop> stops = new ArrayList<>();
        stops.add(stop1);
        stops.add(stop2);

        List<ExtendedRouteBranchStop> sortedStops =  stops.stream().collect(Collectors.toList());

        httpSRI.sortStops(stops);

        assertNotNull(httpSRI);
        assertEquals(stop1.getId(), sortedStops.get(0).getId());
        assertEquals(stop2.getId(), sortedStops.get(1).getId());


    }

    /**
     * Test to make sure that ExtendedRouteBranchStop sorting sorts numerically
     * Have to account for decimals as well
     */
    @Test(expected = Test.None.class /* no exception expected */)
    public void sortMNRStopsTest(){
        HttpRequestStaticRouteInfo httpSRI = new HttpRequestStaticRouteInfo();

        RouteBranchStop rbs1 = new RouteBranchStop("MNR:1", "Penn Station","Hudson","MNR:1", "22");
        ExtendedRouteBranchStop stop1 = new ExtendedRouteBranchStop(rbs1);
        stop1.setLat(40.750588);
        stop1.setLon(-73.993584);

        RouteBranchStop rbs2 = new RouteBranchStop("MNR:4", "Penn Station","Hudson","MNR:1", "2");
        ExtendedRouteBranchStop stop2 = new ExtendedRouteBranchStop(rbs2);
        stop2.setLat(40.750588);
        stop2.setLon(-73.993584);

        RouteBranchStop rbs3 = new RouteBranchStop("MNR:622", "Penn Station","Hudson","MNR:1", "1");
        ExtendedRouteBranchStop stop3 = new ExtendedRouteBranchStop(rbs3);
        stop3.setLat(40.750588);
        stop3.setLon(-73.993584);

        RouteBranchStop rbs4 = new RouteBranchStop("MNR:9", "Penn Station","Hudson","MNR:1", "11");
        ExtendedRouteBranchStop stop4 = new ExtendedRouteBranchStop(rbs4);
        stop4.setLat(40.750588);
        stop4.setLon(-73.993584);

        RouteBranchStop rbs5 = new RouteBranchStop("MNR:10", "University Heights","Hudson","MNR:1", "2.5");
        ExtendedRouteBranchStop stop5 = new ExtendedRouteBranchStop(rbs5);
        stop5.setLat(40.750588);
        stop5.setLon(-73.993584);

        List<ExtendedRouteBranchStop> stops = new ArrayList<>();
        stops.add(stop1);
        stops.add(stop2);
        stops.add(stop3);
        stops.add(stop4);
        stops.add(stop5);

        List<ExtendedRouteBranchStop> sortedStops =  stops.stream().collect(Collectors.toList());
        httpSRI.sortStops(sortedStops);

        assertNotNull(httpSRI);
        assertEquals(stop3.getId(), sortedStops.get(0).getId());
        assertEquals(stop2.getId(), sortedStops.get(1).getId());
        assertEquals(stop5.getId(), sortedStops.get(2).getId());
        assertEquals(stop4.getId(), sortedStops.get(3).getId());
        assertEquals(stop1.getId(), sortedStops.get(4).getId());

    }
}

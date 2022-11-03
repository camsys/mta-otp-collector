package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.google.transit.realtime.GtfsRealtimeMTARR;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;

public class LIRRTripUpdateTransformer extends TripUpdateTransformer {

    private StopIdTransformStrategy _stopIdTransformStrategy;

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe) {
        if (fe.hasTripUpdate()) {
            int delay = 0;
            TripUpdate tu = fe.getTripUpdate();
            TripUpdate.Builder tub = TripUpdate.newBuilder();
            tub.getTripBuilder().setScheduleRelationship(tu.getTrip().getScheduleRelationship());
            String routeId = null, startDate = null, tripId = null;
            Integer directionId = null;
            if (tu.hasTrip() && tu.getTrip().hasTripId()) {
                tripId = tu.getTrip().getTripId();
            }
            if (tu.hasTrip() && tu.getTrip().hasRouteId()) {
                routeId = tu.getTrip().getRouteId();
            }
            if (tu.hasTrip() && tu.getTrip().hasDirectionId()) {
                directionId = tu.getTrip().getDirectionId();
            }
            if (tu.hasTrip() && tu.getTrip().hasStartDate()) {
                startDate = tu.getTrip().getStartDate();
            }


            for (StopTimeUpdate stu : tu.getStopTimeUpdateList()) {
                StopTimeUpdate.Builder stub = stu.toBuilder();
                GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate ext = stub.getExtension(GtfsRealtimeMTARR.mtaRailroadStopTimeUpdate);
                if (ext.hasTrack()) {
                    GtfsRealtimeNYCT.NyctStopTimeUpdate.Builder nyctExt = GtfsRealtimeNYCT.NyctStopTimeUpdate.newBuilder();
                    nyctExt.setActualTrack(ext.getTrack());
                    stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
                }
                if (_stopIdTransformStrategy != null) {
                    String stopId = _stopIdTransformStrategy.transform(null, null, stub.getStopId());
                    stub.setStopId(stopId);
                }
                if (stub.hasDeparture() && !stub.hasArrival()) {
                    stub.setArrival(stub.getDeparture());
                }
                tub.addStopTimeUpdate(stub);
                if (stub.hasDeparture() && stub.getDeparture().hasDelay())
                    delay = stub.getDeparture().getDelay();

            }

            if (tripId != null) {
                tub.getTripBuilder().setTripId(tripId);
            }
            if (startDate != null)
                tub.getTripBuilder().setStartDate(startDate);
            if (routeId != null)
                tub.getTripBuilder().setRouteId(routeId);
            if (directionId != null)
                tub.getTripBuilder().setDirectionId(directionId);

            tub.setDelay(delay);
            return tub;
        }
        return null;
    }

    public void setStopIdTransformStrategy(StopIdTransformStrategy stopIdTransformStrategy) {
        _stopIdTransformStrategy = stopIdTransformStrategy;
    }
}

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
            tub.setDelay(delay);
            return tub;
        }
        return null;
    }

    public void setStopIdTransformStrategy(StopIdTransformStrategy stopIdTransformStrategy) {
        _stopIdTransformStrategy = stopIdTransformStrategy;
    }
}

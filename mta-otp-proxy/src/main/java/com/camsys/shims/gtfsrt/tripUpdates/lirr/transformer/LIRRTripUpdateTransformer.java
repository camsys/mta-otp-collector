package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtimeLIRR;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;

public class LIRRTripUpdateTransformer extends TripUpdateTransformer {

    private StopIdTransformStrategy _stopIdTransformStrategy;

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe) {
        if (fe.hasTripUpdate()) {
            TripUpdate.Builder tripUpdate = fe.getTripUpdate().toBuilder();
            for (StopTimeUpdate.Builder stub : tripUpdate.getStopTimeUpdateBuilderList()) {
                String track = stub.getExtension(GtfsRealtimeLIRR.MtaStopTimeUpdate.track);
                if (track != null) {
                    GtfsRealtimeNYCT.NyctStopTimeUpdate.Builder nyctExt = GtfsRealtimeNYCT.NyctStopTimeUpdate.newBuilder();
                    nyctExt.setActualTrack(track);
                    stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
                }
                // need to remove extension so downstream systems (OTP) don't try to read it as MnrStopTimeUpdate
                stub.clearExtension(GtfsRealtimeLIRR.MtaStopTimeUpdate.track);
                if (_stopIdTransformStrategy != null) {
                    String stopId = _stopIdTransformStrategy.transform(null, null, stub.getStopId());
                    stub.setStopId(stopId);
                }
            }
            return tripUpdate;
        }
        return null;
    }

    public void setStopIdTransformStrategy(StopIdTransformStrategy stopIdTransformStrategy) {
        _stopIdTransformStrategy = stopIdTransformStrategy;
    }
}

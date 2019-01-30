package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtimeLIRR;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.kurtraschke.nyctrtproxy.model.MatchMetrics;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;

public class LIRRTripUpdateTransformer extends TripUpdateTransformer {

    private static final String JAMAICA = "15";

    private StopIdTransformStrategy _stopIdTransformStrategy;

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe, MatchMetrics matchMetrics) {
        if (fe.hasTripUpdate()) {
            TripUpdate.Builder tripUpdate = fe.getTripUpdate().toBuilder();
            if (ignoreTripUpdate(tripUpdate)) {
                return null;
            }
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

    // Per-MOTP-796: temporarily ignore TripUpdates created by the track assignment system at Jamaica
    private boolean ignoreTripUpdate(TripUpdate.Builder tu) {
        return tu.getStopTimeUpdateCount() > 0 && JAMAICA.equals(tu.getStopTimeUpdate(0).getStopId());
    }

    public void setStopIdTransformStrategy(StopIdTransformStrategy stopIdTransformStrategy) {
        _stopIdTransformStrategy = stopIdTransformStrategy;
    }
}

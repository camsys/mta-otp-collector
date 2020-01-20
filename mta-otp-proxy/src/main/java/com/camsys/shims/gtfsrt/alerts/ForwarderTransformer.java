package com.camsys.shims.gtfsrt.alerts;

import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * copy input GTFS-RT to output without transformation.
 */
public class ForwarderTransformer implements GtfsRealtimeTransformer<GtfsRealtime.FeedMessage> {

    private static Logger _log = LoggerFactory.getLogger(ForwarderTransformer.class);

    @Override
    public GtfsRealtime.FeedMessage transform(GtfsRealtime.FeedMessage obj) {
        return obj;
    }
}

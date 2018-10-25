package com.camsys.shims.util.transformer;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public interface GtfsRealtimeTransformer<T> {
    FeedMessage transform(T obj);
}

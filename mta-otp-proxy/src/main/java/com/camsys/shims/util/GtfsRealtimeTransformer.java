package com.camsys.shims.util;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public interface GtfsRealtimeTransformer<T> {
    FeedMessage transform(T obj);
}

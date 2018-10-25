package com.camsys.shims.util.transformer;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

/**
 * <p>GtfsRealtimeTransformer interface.</p>
 *
 */
public interface GtfsRealtimeTransformer<T> {
    /**
     * <p>transform.</p>
     *
     * @param obj a T object.
     * @return a {@link com.google.transit.realtime.GtfsRealtime.FeedMessage} object.
     */
    FeedMessage transform(T obj);
}

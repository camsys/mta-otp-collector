package com.camsys.shims.util.source;

import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;

/**
 * <p>UpdatingGtfsRealtimeSource interface.</p>
 *
 */
public interface UpdatingGtfsRealtimeSource extends GtfsRealtimeSource {
    /**
     * <p>update.</p>
     */
    void update();
}

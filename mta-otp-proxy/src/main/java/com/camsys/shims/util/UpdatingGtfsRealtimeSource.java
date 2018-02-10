package com.camsys.shims.util;

import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;

public interface UpdatingGtfsRealtimeSource extends GtfsRealtimeSource {
    void update();
}

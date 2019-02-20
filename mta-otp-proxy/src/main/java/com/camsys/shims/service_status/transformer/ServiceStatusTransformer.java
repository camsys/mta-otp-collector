package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import org.onebusaway.gtfs.services.GtfsDataService;

import java.util.List;
import java.util.Map;

public interface ServiceStatusTransformer<T> {
    List<RouteDetail> transform(T obj, String mode, GtfsDataService gtfsDataService, GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap);
}

package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.util.gtfs.GtfsAndCalendar;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.List;
import java.util.Map;

/**
 * <p>ServiceStatusTransformer interface.</p>
 *
 */
public interface ServiceStatusTransformer<T> {
    /**
     * <p>transform.</p>
     *
     * @param obj a T object.
     * @param mode a {@link java.lang.String} object.
     * @param gtfsAndCalendar a {@link com.camsys.shims.util.gtfs.GtfsAndCalendar} object.
     * @param gtfsAdapter a {@link com.camsys.shims.service_status.adapters.GtfsRouteAdapter} object.
     * @param _routeDetailsMap a {@link java.util.Map} object.
     * @return a {@link java.util.List} object.
     */
    List<RouteDetail> transform(T obj, String mode, GtfsAndCalendar gtfsAndCalendar, GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap);
}

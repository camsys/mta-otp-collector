package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.model.RouteDetail;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.List;

public interface ServiceStatusTransformer<T> {
    List<RouteDetail> transform(T obj, String mode, GtfsRelationalDao dao, CalendarServiceData csd);
}

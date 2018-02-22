package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.camsys.shims.util.TimeUtils.getCurrentDateTime;

public class MergingServiceStatusSource implements ServiceStatusSource
{

    private static final Logger _log = LoggerFactory.getLogger(MergingServiceStatusSource.class);

    private List<ServiceStatusSource> _sources;

    private ServiceStatus _serviceStatus;

    public MergingServiceStatusSource(List<ServiceStatusSource> sources) {
        _sources = sources;
    }

    @Override
    public void update() {
        List<RouteDetail> allRouteDetails = new ArrayList<>();
        for (ServiceStatusSource source : _sources) {
            source.update();
            allRouteDetails.addAll(source.getStatus(null).getRouteDetailList());
        }
        _serviceStatus = new ServiceStatus(new Date(), allRouteDetails);

    }

    @Override
    public ServiceStatus getStatus(String updatesSince) {
        filterServiceStatus(updatesSince);
        return _serviceStatus;
    }

    private void filterServiceStatus(String updatesSince){
        if(updatesSince != null) {
            try {
                final Date updatesSinceDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatesSince);
                List<RouteDetail> routeDetails = _serviceStatus.getRouteDetailList();

                _serviceStatus.setRouteDetailList(routeDetails.stream()
                        .filter(routeDetail -> routeDetail.getStatusDetailsList() == null ||
                                routeDetail.getStatusDetailsList().stream()
                                        .allMatch(status -> updatesSinceDate.compareTo(status.getCreationDate()) <= 0))
                        .collect(Collectors.toList()));

            } catch (ParseException pe) {
                _log.error("Unable to parse updatesSince date param {}", updatesSince);
            }
        }
    }
}

package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;

import java.util.ArrayList;
import java.util.List;

import static com.camsys.shims.util.TimeUtils.getCurrentDateTime;

public class MergingServiceStatusSource implements ServiceStatusSource
{
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
        _serviceStatus = new ServiceStatus(getCurrentDateTime(), allRouteDetails);

    }

    @Override
    public ServiceStatus getStatus(String updatesSince) {
        return _serviceStatus;
    }
}

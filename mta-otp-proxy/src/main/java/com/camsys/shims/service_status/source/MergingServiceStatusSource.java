package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.camsys.shims.util.TimeUtils.getCurrentDateTime;

/**
 * <p>MergingServiceStatusSource class.</p>
 *
 */
public class MergingServiceStatusSource implements ServiceStatusSource
{

    private static final Logger _log = LoggerFactory.getLogger(MergingServiceStatusSource.class);

    private List<ServiceStatusSource> _sources;

    private ServiceStatus _serviceStatus;

    /**
     * <p>Constructor for MergingServiceStatusSource.</p>
     *
     * @param sources a {@link java.util.List} object.
     */
    public MergingServiceStatusSource(List<ServiceStatusSource> sources) {
        _sources = sources;
    }

    /** {@inheritDoc} */
    @Override
    public void update() {
        List<RouteDetail> allRouteDetails = new ArrayList<>();
        for (ServiceStatusSource source : _sources) {
            source.update();
            allRouteDetails.addAll(source.getStatus(null).getRouteDetails());
        }
        _serviceStatus = new ServiceStatus(new Date(), allRouteDetails);

    }

    /** {@inheritDoc} */
    @Override
    public ServiceStatus getStatus(String updatesSince) {
        if(updatesSince != null)
            return getFilteredServiceStatus(updatesSince);
        return _serviceStatus;
    }

    private ServiceStatus getFilteredServiceStatus(String updatesSince){
        try {
            final Date updatesSinceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(updatesSince);
            List<RouteDetail> routeDetails = _serviceStatus.getRouteDetails().stream()
                    .filter(routeDetail -> updatesSinceDate.compareTo(routeDetail.getLastUpdated()) <= 0)
                    .collect(Collectors.toList());

            return new ServiceStatus(_serviceStatus.getLastUpdated(), routeDetails);

        } catch (ParseException pe) {
            _log.error("Unable to parse updatesSince date param {}", updatesSince);
        }
        return _serviceStatus;
    }
}

package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.transformer.ServiceStatusTransformer;
import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.gtfs.GtfsAndCalendar;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>TransformingServiceStatusSource class.</p>
 *
 */
public class TransformingServiceStatusSource<T> implements ServiceStatusSource
{
    private static final Logger _log = LoggerFactory.getLogger(TransformingServiceStatusSource.class);

    protected ServiceStatus _serviceStatus;

    protected ServiceStatusTransformer _transformer;

    protected GtfsRouteAdapter _gtfsAdapter;

    protected HttpClientConnectionManager _connectionManager;

    protected CloseableHttpClient _httpClient;

    protected Deserializer<T> _deserializer;

    protected int _nTries = 5;

    protected int _retryDelay = 5;

    protected String _sourceUrl;

    protected String _mode;

    protected GtfsAndCalendar _gtfsAndCalendar;

    protected List<String> _excludeRoutes;

    protected List<String> _inServiceFalseRoutes;

    protected List<String> _inServiceTrueRoutes;

    private Map<String, RouteDetail> _routeDetailsMap = new HashMap<>();

    /**
     * <p>setSourceUrl.</p>
     *
     * @param sourceUrl a {@link java.lang.String} object.
     */
    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    /**
     * <p>setMode.</p>
     *
     * @param mode a {@link java.lang.String} object.
     */
    public void setMode(String mode) {
        _mode = mode;
    }

    /**
     * <p>setTransformer.</p>
     *
     * @param transformer a {@link com.camsys.shims.service_status.transformer.ServiceStatusTransformer} object.
     */
    public void setTransformer(ServiceStatusTransformer transformer) {
        _transformer = transformer;
    }

    /**
     * <p>setGtfsRouteAdapter.</p>
     *
     * @param gtfsAdapter a {@link com.camsys.shims.service_status.adapters.GtfsRouteAdapter} object.
     */
    public void setGtfsRouteAdapter(GtfsRouteAdapter gtfsAdapter) {
        _gtfsAdapter = gtfsAdapter;
    }

    /**
     * <p>setDeserializer.</p>
     *
     * @param deserializer a {@link com.camsys.shims.util.deserializer.Deserializer} object.
     */
    public void setDeserializer(Deserializer<T> deserializer) {
        _deserializer = deserializer;
    }

    /**
     * <p>setGtfsAndCalendar.</p>
     *
     * @param gtfsAndCalendar a {@link com.camsys.shims.util.gtfs.GtfsAndCalendar} object.
     */
    public void setGtfsAndCalendar(GtfsAndCalendar gtfsAndCalendar) {
        _gtfsAndCalendar = gtfsAndCalendar;
    }

    /** {@inheritDoc} */
    @Override
    public void update() {
        T sourceData = getSourceData(_sourceUrl, _deserializer);
        if (sourceData != null) {
            List<RouteDetail>  routeDetails = _transformer
                    .transform(sourceData, _mode, _gtfsAndCalendar, _gtfsAdapter, _routeDetailsMap);
            if (_excludeRoutes != null && !_excludeRoutes.isEmpty()) {
                routeDetails = routeDetails.stream()
                        .filter(r -> !_excludeRoutes.contains(r.getRouteId()))
                        .collect(Collectors.toList());
            }
            if (_inServiceFalseRoutes != null && !_inServiceFalseRoutes.isEmpty()) {
                for (RouteDetail routeDetail : routeDetails) {
                    if (_inServiceFalseRoutes.contains(routeDetail.getRouteId())) {
                        routeDetail.setInService(false);
                    }
                }
            }
            if (_inServiceTrueRoutes != null && !_inServiceTrueRoutes.isEmpty()) {
                for (RouteDetail routeDetail : routeDetails) {
                    if (_inServiceTrueRoutes.contains(routeDetail.getRouteId())) {
                        routeDetail.setInService(true);
                    }
                }
            }
            _serviceStatus = new ServiceStatus(new Date(), routeDetails);
        }
    }

    /**
     * <p>getSourceData.</p>
     *
     * @param sourceUrl a {@link java.lang.String} object.
     * @param deserializer a {@link com.camsys.shims.util.deserializer.Deserializer} object.
     * @return a T object.
     */
    protected T getSourceData(String sourceUrl, Deserializer<T> deserializer){
        if (_httpClient == null)
            _httpClient = HttpClientBuilder.create().setConnectionManager(_connectionManager).build();

        HttpGet get = new HttpGet(sourceUrl);
        get.setHeader("accept", deserializer.getMimeType());

        T siri = null;
        for (int tries = 0; tries < _nTries; tries++) {
            try {
                CloseableHttpResponse response = _httpClient.execute(get);
                try (InputStream streamContent = response.getEntity().getContent()) {
                    siri = deserializer.deserialize(streamContent);
                    if (siri != null)
                        return siri;
                    Thread.sleep(_retryDelay * 1000);
                }
            } catch (Exception e) {
                _log.error("Error parsing protocol feed: {}", sourceUrl);
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ServiceStatus getStatus(String updatesSince) {
        return _serviceStatus;
    }

    /**
     * <p>setExcludeRoutes.</p>
     *
     * @param excludeRoutes a {@link java.lang.String} object.
     */
    public void setExcludeRoutes(String excludeRoutes) {
        _excludeRoutes = Arrays.asList(excludeRoutes.split(","));
    }

    /**
     * <p>setInServiceFalseRoutes.</p>
     *
     * @param inServiceFalseRoutes a {@link java.lang.String} object.
     */
    public void setInServiceFalseRoutes(String inServiceFalseRoutes) {
        _inServiceFalseRoutes = Arrays.asList(inServiceFalseRoutes.split(","));
    }

    /**
     * <p>setInServiceTrueRoutes.</p>
     *
     * @param inServiceTrueRoutes a {@link java.lang.String} object.
     */
    public void setInServiceTrueRoutes(String inServiceTrueRoutes) {
        _inServiceTrueRoutes = Arrays.asList(inServiceTrueRoutes.split(","));
    }
}

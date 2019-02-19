package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.transformer.ServiceStatusTransformer;
import com.camsys.shims.util.deserializer.Deserializer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    protected GtfsDataService _gtfsDataService;

    protected List<String> _excludeRoutes;

    protected List<String> _inServiceFalseRoutes;

    protected List<String> _inServiceTrueRoutes;

    private Map<String, RouteDetail> _routeDetailsMap = new HashMap<>();

    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    public void setMode(String mode) {
        _mode = mode;
    }

    public void setTransformer(ServiceStatusTransformer transformer) {
        _transformer = transformer;
    }

    public void setGtfsRouteAdapter(GtfsRouteAdapter gtfsAdapter) {
        _gtfsAdapter = gtfsAdapter;
    }

    public void setDeserializer(Deserializer<T> deserializer) {
        _deserializer = deserializer;
    }

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
        _gtfsDataService = gtfsDataService;
    }

    @Override
    public void update() {
        T sourceData = getSourceData(_sourceUrl, _deserializer);
        if (sourceData != null) {
            List<RouteDetail>  routeDetails = _transformer
                    .transform(sourceData, _mode, _gtfsDataService, _gtfsAdapter, _routeDetailsMap);
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

    @Override
    public ServiceStatus getStatus(String updatesSince) {
        return _serviceStatus;
    }

    public void setExcludeRoutes(String excludeRoutes) {
        _excludeRoutes = Arrays.asList(excludeRoutes.split(","));
    }

    public void setInServiceFalseRoutes(String inServiceFalseRoutes) {
        _inServiceFalseRoutes = Arrays.asList(inServiceFalseRoutes.split(","));
    }

    public void setInServiceTrueRoutes(String inServiceTrueRoutes) {
        _inServiceTrueRoutes = Arrays.asList(inServiceTrueRoutes.split(","));
    }
}

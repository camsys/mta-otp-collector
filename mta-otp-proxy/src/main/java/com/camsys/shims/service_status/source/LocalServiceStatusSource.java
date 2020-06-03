package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.transformer.ServiceStatusTransformer;
import com.camsys.shims.util.deserializer.Deserializer;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lcaraballo on 4/5/18.
 */
public class LocalServiceStatusSource<T> implements ServiceStatusSource {

    private static final Logger _log = LoggerFactory.getLogger(LocalServiceStatusSource.class);

    protected Deserializer<T> _deserializer;

    protected ServiceStatusTransformer _transformer;

    protected String _sourceUrl;

    protected String _mode;

    protected List<GtfsDataService> _gtfsDataServices = new ArrayList<GtfsDataService>();

    private Map<String, RouteDetail> _routeDetailsMap = new HashMap<>();

    protected GtfsRouteAdapter _gtfsAdapter;

    protected ServiceStatus _serviceStatus;

    public void setDeserializer(Deserializer<T> deserializer) {
        _deserializer = deserializer;
    }

    public void setGtfsRouteAdapter(GtfsRouteAdapter _gtfsAdapter) {
        _gtfsAdapter = _gtfsAdapter;
    }
    public void setMode(String _mode) {
        _mode = _mode;
    }

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
        if (gtfsDataService != null) {
            _gtfsDataServices.add(gtfsDataService);
        }
    }

    public void setRouteDetailsMap(Map<String, RouteDetail> _routeDetailsMap) {
        _routeDetailsMap = _routeDetailsMap;
    }

    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    public void setTransformer(ServiceStatusTransformer transformer) {
        _transformer = transformer;
    }

    @Override
    public void update() {
        T sourceData = getSourceData(_sourceUrl, _deserializer);
        if (sourceData != null) {
            List<RouteDetail> routeDetails = _transformer
                    .transform(sourceData, _mode, _gtfsDataServices, _gtfsAdapter, _routeDetailsMap);
            _serviceStatus = new ServiceStatus(new Date(), routeDetails);
        }
    }

    private T getSourceData(String filePath, Deserializer<T> deserializer){
        try {
            InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream(filePath);
            return deserializer.deserialize(is);
        } catch(Exception e){
            _log.error("Error processing file: {}", filePath);
        }
        return null;
    }

    @Override
    public ServiceStatus getStatus(String updatesSince) {
        return _serviceStatus;
    }
}

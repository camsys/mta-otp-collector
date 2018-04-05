package com.camsys.shims.service_status.source;

import com.camsys.mta.gms_service_status.Service;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.transformer.GmsServiceStatusTransformer;
import com.camsys.shims.service_status.transformer.ServiceStatusTransformer;
import com.camsys.shims.util.deserializer.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by lcaraballo on 4/5/18.
 */
public class LocalServiceStatusSource<T> implements ServiceStatusSource {

    private static final Logger _log = LoggerFactory.getLogger(LocalServiceStatusSource.class);

    protected Deserializer<T> _deserializer;

    protected String _sourceUrl;

    protected ServiceStatusTransformer _transformer;

    protected ServiceStatus _serviceStatus;

    public void setDeserializer(Deserializer<T> deserializer) {
        _deserializer = deserializer;
    }

    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    public void setTransformer(ServiceStatusTransformer transformer) {
        _transformer = transformer;
    }

    @Override
    public void update() {
        T service = getService(_sourceUrl, _deserializer);
        if (service != null) {
            List<RouteDetail> routeDetails = _transformer
                    .transform(service, null, null, null, null);
            _serviceStatus = new ServiceStatus(new Date(), routeDetails);
        }
    }

    private T getService(String filePath, Deserializer<T> deserializer){
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

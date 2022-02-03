package com.camsys.shims.schedule.transformer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.onebusaway.cloud.api.ExternalResult;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;
import org.onebusaway.cloud.api.InputStreamConsumer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GeojsonProvider {
    private String url;

    private ObjectMapper _mapper;

    private boolean _cache = true;

    private FeatureCollection geojson;

    private ExternalServices _externalServices = new ExternalServicesBridgeFactory().getExternalServices();

    public GeojsonProvider(String url) {
        this.url = url;
        _mapper = new ObjectMapper();
        _mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public FeatureCollection getGeojson() throws Exception {
        if (_cache && geojson != null) {
            return geojson;
        }
        if (url.startsWith("file://")) {
        	String input = Files.readString(new File(url.replace("file://", "")).toPath());        	
            geojson = _mapper.readValue(input, FeatureCollection.class);
        	return geojson;
            
        } else if (url.startsWith("s3://")) {
            ExternalResult result = _externalServices.getFileAsStream(url, new InputStreamConsumer() {
                @Override
                public void accept(InputStream input) throws IOException {
                    geojson = _mapper.readValue(input, FeatureCollection.class);
                }
            });
            if (result.getSuccess()) {
                return geojson;
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }
    }

    public void setCache(boolean cache) {
        _cache = cache;
    }
}

package com.camsys.shims.schedule.transformer;

import com.amazonaws.services.s3.AmazonS3;
import com.camsys.shims.s3.S3Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;

public class GeojsonProvider {
    private String url;

    private AmazonS3 s3 = null;

    private ObjectMapper _mapper;

    private boolean _cache = true;

    private FeatureCollection geojson;

    public GeojsonProvider(String url, String s3key, String s3pass) {
        this.url = url;
        s3 = S3Utils.getS3Client(s3key, s3pass);
        _mapper = new ObjectMapper();
        _mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public FeatureCollection getGeojson() {
        if (_cache && geojson != null) {
            return geojson;
        }
        InputStream input = null;
        if (url.startsWith("s3://")) {
            input = S3Utils.getViaS3(s3, url);
        } else {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }
        try {
            geojson = _mapper.readValue(input, FeatureCollection.class);
            input.close();
            return geojson;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCache(boolean cache) {
        _cache = cache;
    }
}

package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * serve static JSON data backed by arbitrary CSV at a url.
 */
public class HttpRequestStaticData implements HttpRequestHandler {

    private String s3key = null;
    public void setS3key(String key) {
        this.s3key = key;
    }
    private String s3pass = null;
    public void setS3pass(String pass) {
        this.s3pass = pass;
    }

    private String _sourceUrl = null;
    public void setSourceUrl(String url) {
        _sourceUrl = url;
    }
    private CsvRecordReader reader = null;
    public void setCsvRecord(CsvRecordReader reader) {
        this.reader = reader;
    }
    private boolean _cacheResults = true;
    public void setCacheResults(boolean cacheResults) {
        this._cacheResults = cacheResults;
    }

    private CsvToJsonTransformer _transformer = null;
    private static ObjectMapper mapper = new ObjectMapper();
    private Map<String, List<Object>> _cache = new HashMap<>();


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (_transformer == null) {
            _transformer = getTransformer();
        }

        String routeId = req.getParameter("routeId");

        if (_cacheResults) {
            if (_cache.containsKey(routeId)) {
                ObjectWriter writer = mapper.writer();
                writer.writeValue(resp.getWriter(), _cache.get(routeId));
                return;
            }
        }

        // lookup injected source file
        // download and load
        getTransformer().loadUrl(_sourceUrl);
        // filter
        // transform
        List<Object> obj = getTransformer().transform(routeId);
        // serve -- if not caching, pretty print it for easy reading
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), obj);

        if (_cacheResults) {
            _cache.put(routeId, obj);
        }

    }

    private CsvToJsonTransformer getTransformer() {
        if (_transformer == null) {
            _transformer = new CsvToJsonTransformer(reader, s3key, s3pass);
        }
        return _transformer;
    }
}

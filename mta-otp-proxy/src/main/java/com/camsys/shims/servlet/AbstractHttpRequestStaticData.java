package com.camsys.shims.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * serve static JSON data backed by something on S3
 */
public abstract class AbstractHttpRequestStaticData<T> implements HttpRequestHandler {

    protected String s3key = null;

    public void setS3key(String key) {
        this.s3key = key;
    }

    protected String s3pass = null;

    public void setS3pass(String pass) {
        this.s3pass = pass;
    }

    private boolean _cacheResults = true;

    public void setCacheResults(boolean cacheResults) {
        this._cacheResults = cacheResults;
    }

    private static ObjectMapper mapper = new ObjectMapper();

    private Map<String, T> _cache = new HashMap<>();

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String routeId = req.getParameter("routeId");

        if (_cacheResults) {
            if (_cache.containsKey(routeId)) {
                ObjectWriter writer = mapper.writer();
                writer.writeValue(resp.getWriter(), _cache.get(routeId));
                return;
            }
        }

        T obj = getData(routeId);

        // serve -- if not caching, pretty print it for easy reading
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), obj);

        if (_cacheResults) {
            _cache.put(routeId, obj);
        }

    }

    protected abstract T getData(String param);
}

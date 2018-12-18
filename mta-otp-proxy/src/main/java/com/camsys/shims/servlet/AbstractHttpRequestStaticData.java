package com.camsys.shims.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * serve static JSON data backed by something on S3
 */
public abstract class AbstractHttpRequestStaticData<T> implements HttpRequestHandler {

    private static final String CONTENT_TYPE = "application/json";

    private boolean _cacheResults = true;

    public void setCacheResults(boolean cacheResults) {
        this._cacheResults = cacheResults;
    }

    private int _cacheExpireSec = -1;

    public void setCacheExpireSec(int cacheExpireSec) {
        _cacheExpireSec = cacheExpireSec;
    }

    private static ObjectMapper mapper = new ObjectMapper();

    private Cache<String, T> _cache;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(CONTENT_TYPE);

        String routeId = req.getParameter("routeId");

        if (_cacheResults) {
            if (getCache().getIfPresent(routeId) != null) {
                ObjectWriter writer = mapper.writer();
                writer.writeValue(resp.getWriter(), _cache.getIfPresent(routeId));
                return;
            }
        }

        T obj = getData(routeId);

        // serve -- if not caching, pretty print it for easy reading
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), obj);

        if (_cacheResults) {
            getCache().put(routeId, obj);
        }

    }

    private Cache<String, T> getCache() {
        if (_cache == null) {
            if (_cacheExpireSec < 0) {
                _cache = CacheBuilder.newBuilder().build();
            } else {
                _cache = CacheBuilder.newBuilder()
                        .expireAfterWrite(_cacheExpireSec, TimeUnit.SECONDS)
                        .build();
            }
        }
        return _cache;
    }

    protected abstract T getData(String param);
}

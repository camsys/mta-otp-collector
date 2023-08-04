package com.camsys.shims.servlet;

import com.camsys.shims.healthcheck.HealthcheckModel;
import com.camsys.shims.util.source.RealTimeMonitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class HttpRequestHealthcheck implements HttpRequestHandler {

    private static final String CONTENT_TYPE = "application/json";

    private static final Logger _log = LoggerFactory.getLogger(HttpRequestHealthcheck.class);

    private static ObjectMapper _mapper = new ObjectMapper();

    private RealTimeMonitor _monitor;

    private String _hostname = "localhost";

    private int _port = 8080;

    /** allow the service status last updated time to be this old */
    private int _gracePeriodSec = 120;

    private int _minStopsForRoute = 10;

    public void setMonitor(RealTimeMonitor monitor) {
        _monitor = monitor;
    }

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        HealthcheckModel status = getHealthcheck();
        ObjectWriter writer = _mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(resp.getWriter(), status);
    }

    private HealthcheckModel getHealthcheck() throws IOException {
        // ideally we want a date that proves we are still doing something interesting
        // for now we lie and give our date
        return new HealthcheckModel(new Date(), 0, _monitor);
    }

    public void setHostname(String hostname) {
        _hostname = hostname;
    }

    public void setPort(int port) {
        _port = port;
    }

    public void setGracePeriodSec(int gracePeriodSec) {
        _gracePeriodSec = gracePeriodSec;
    }

    public void setMinStopsForRoute(int stopsForRoute) {
        _minStopsForRoute = stopsForRoute;
    }
}

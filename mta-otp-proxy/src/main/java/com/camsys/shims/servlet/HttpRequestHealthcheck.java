package com.camsys.shims.servlet;

import com.camsys.shims.healthcheck.HealthcheckModel;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class HttpRequestHealthcheck implements HttpRequestHandler {

    private static final Logger _log = LoggerFactory.getLogger(HttpRequestHealthcheck.class);

    private static ObjectMapper _mapper = new ObjectMapper();

    private String _hostname = "localhost";

    private int _port = 8080;

    /** allow the service status last updated time to be this old */
    private int _gracePeriodSec = 120;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HealthcheckModel status = getHealthcheck();
        ObjectWriter writer = _mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), status);
    }

    private HealthcheckModel getHealthcheck() throws IOException {
        // check service status
        URL serviceStatusUrl = new URL("http://" + _hostname + ":" + _port + "/realtime/serviceStatus");
        ServiceStatus status = _mapper.readValue(serviceStatusUrl, ServiceStatus.class);

        if ((new Date().getTime() - status.getLastUpdated().getTime()) > (_gracePeriodSec * 1000)) {
            throw new RuntimeException("Service status API is too old.");
        }

        // check stops for route
        URL stopsForRouteUtl = new URL("http://" + _hostname + ":" + _port + "/schedule/stopsForRoute");
        List<?> stopsForRoute = _mapper.readValue(stopsForRouteUtl, List.class);

        if (stopsForRoute.size() < 10) {
            throw new RuntimeException("no stops for route in data!");
        }

        return new HealthcheckModel(status.getLastUpdated(), stopsForRoute.size());
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
}

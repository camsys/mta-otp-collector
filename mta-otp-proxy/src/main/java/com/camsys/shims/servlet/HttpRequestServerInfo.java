package com.camsys.shims.servlet;

import com.camsys.shims.server_info.ServerInfoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.web.HttpRequestHandler;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HttpRequestServerInfo implements HttpRequestHandler{

    private static final String CONTENT_TYPE = "application/json";
    private static ObjectMapper _mapper = new ObjectMapper();

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        ServerInfoModel info = getServerInfo();
        ObjectWriter writer = _mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), info);
    }

    private ServerInfoModel getServerInfo() throws IOException {
        return new ServerInfoModel();
    }
}

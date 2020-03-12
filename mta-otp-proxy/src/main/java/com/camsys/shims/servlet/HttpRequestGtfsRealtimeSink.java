/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.servlet;

import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpRequestGtfsRealtimeSink implements HttpRequestHandler {

    private static final String CONTENT_TYPE = "application/x-google-protobuf";

    private static final String DEBUG_CONTENT_TYPE = "text/plain";

    private static final String JSON_CONTENT_TYPE = "application/json";

    private GtfsRealtimeSource _source;

    public void setSource(GtfsRealtimeSource source) {
        _source = source;
    }

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean debug = req.getParameter("debug") != null;
        boolean json = "json".equals(req.getParameter("type"));

        Message message = _source.getFeed();
        if (debug) {
            resp.setContentType(DEBUG_CONTENT_TYPE);
            resp.getWriter().print(message);
        } else if (json) {
            if (message != null) {
                resp.setContentType(JSON_CONTENT_TYPE);
                resp.getWriter().print(JsonFormat.printToString(message));
            } else {
                resp.setContentType(DEBUG_CONTENT_TYPE);
                resp.getWriter().print(message);
            }
        } else {
            resp.setContentType(CONTENT_TYPE);
            message.writeTo(resp.getOutputStream());
        }
    }

}

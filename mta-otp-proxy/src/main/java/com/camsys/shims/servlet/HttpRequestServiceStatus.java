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

import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.source.ServiceStatusSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>HttpRequestServiceStatus class.</p>
 *
 */
public class HttpRequestServiceStatus implements HttpRequestHandler {

    private static final String CONTENT_TYPE = "application/json";

    private static ObjectMapper mapper = new ObjectMapper();

    private ServiceStatusSource _source;

    /**
     * <p>setSource.</p>
     *
     * @param source a {@link com.camsys.shims.service_status.source.ServiceStatusSource} object.
     */
    public void setSource(ServiceStatusSource source) {
        _source = source;
    }

    /** {@inheritDoc} */
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        String updatesSince = req.getParameter("updatesSince");
        ServiceStatus status = _source.getStatus(updatesSince);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(resp.getWriter(), status);
    }
}

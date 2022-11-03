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

import com.camsys.shims.stoptimes.StopTimesList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.kurtraschke.nyctrtproxy.FeedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class HttpRequestSubwayStopTimes implements HttpRequestHandler {

    private static Logger _log = LoggerFactory.getLogger(HttpRequestSubwayStopTimes.class);
    private FeedManager _feedManager;
    public void setFeedManager(FeedManager feedManager) {
        _feedManager = feedManager;
    }

    private Multimap<String, String> _routeToFeedMap = ImmutableMultimap.<String, String>builder()
            .putAll("1",    "1", "2", "3", "4", "5", "6", "GS")
            .putAll("26",   "A", "C", "E", "H", "GS")
            .putAll("16",   "N", "Q", "R", "W")
            .putAll("21",   "B", "D", "F", "M")
            .putAll("2",    "L")
            .putAll("11",   "SI")
            .putAll("31",   "G")
            .putAll("36",   "J", "Z")
            .putAll("51",   "7")
            .build()
            .inverse();

    private static ObjectMapper _mapper = new ObjectMapper();

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String routeId = req.getParameter("route");
        String stopId = req.getParameter("stop");
        if (routeId == null | stopId == null)
            throw new ServletException("Supply routeId and stopId");
        String feedId = _routeToFeedMap.get(routeId).iterator().next();
        String feedUrl = _feedManager.getFeedOrDefault(feedId);
        InputStream streamContent = _feedManager.getStream(feedUrl, feedId);
        FeedMessage message = FeedMessage.parseFrom(streamContent);
        StopTimesList stopTimes = new StopTimesList(message, routeId, stopId);
        resp.setHeader("Content-Type", "application/json");
        _mapper.writer().writeValue(resp.getWriter(), stopTimes);
    }



}

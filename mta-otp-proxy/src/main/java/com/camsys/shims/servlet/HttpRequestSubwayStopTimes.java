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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;
import org.onebusaway.cloud.api.InputStreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestSubwayStopTimes implements HttpRequestHandler {

    private static Logger _log = LoggerFactory.getLogger(HttpRequestSubwayStopTimes.class);

    private static final String BASE_URL = "http://api.mta.info/mta_esi.php";

    private String _url = BASE_URL;

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

    public void setBaseUrl(String url) {
        _url = url;
    }

    private Map<String, String> _feedToUrlMap = new HashMap<>();
    public java.util.Map<String, String> getFeedToUrlMap() {
        return _feedToUrlMap;
    }
    public void setOverrideFeedToUrl(Map<String, String> kv) {
        for (String key: kv.keySet()) {
            _feedToUrlMap.put(key, kv.get(key));
        }
    }

    private Map<String, String> _feedToProfileMap = new HashMap<>();
    public Map<String, String> getFeedToProfileMap() {
        return _feedToProfileMap;
    }
    public void setFeedToProfile(Map<String, String> kv) {
        for (String key: kv.keySet()) {
            _feedToProfileMap.put(key, kv.get(key));
        }
    }

    private Map<String, String> _feedToRegionMap = new HashMap<>();
    public Map<String, String> getFeedToRegionMap() { return _feedToRegionMap; }
    public void setFeedToRegion(Map<String, String> kv) {
        for (String key: kv.keySet()) {
            _feedToRegionMap.put(key, kv.get(key));
        }
    }

    private String _key;

    private HttpClientConnectionManager _connectionManager;

    private CloseableHttpClient _httpClient;

    private static ObjectMapper _mapper = new ObjectMapper();

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (_httpClient == null)
            _httpClient = HttpClientBuilder.create().setConnectionManager(_connectionManager).build();

        String routeId = req.getParameter("route");
        String stopId = req.getParameter("stop");
        if (routeId == null | stopId == null)
            throw new ServletException("Supply routeId and stopId");
        String feedId = _routeToFeedMap.get(routeId).iterator().next();
        URI feedUrl;
        try {
            feedUrl = getFeed(String.valueOf(feedId));
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        InputStream streamContent = getStream(feedUrl, feedId);
        FeedMessage message = FeedMessage.parseFrom(streamContent);
        StopTimesList stopTimes = new StopTimesList(message, routeId, stopId);
        resp.setHeader("Content-Type", "application/json");
        _mapper.writer().writeValue(resp.getWriter(), stopTimes);
    }

    private InputStream getStream(URI feedUrl, String feedId) throws IOException {
        if (feedUrl.getScheme().toLowerCase().startsWith("http")) {
            HttpGet get = new HttpGet(feedUrl);
            CloseableHttpResponse response = _httpClient.execute(get);
            InputStream streamContent = response.getEntity().getContent();
            return streamContent;
        }
        if (feedUrl.getScheme().toLowerCase().startsWith("s3")) {
            ExternalServices es = getExternalServices();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            es.getFileAsStream(feedUrl.toString(), new InputStreamConsumer() {
                @Override
                public void accept(InputStream inputStream) throws IOException {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > -1 ) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                }
            }, _feedToProfileMap.get(feedId));
            return new ByteArrayInputStream(baos.toByteArray());
        }
        throw new IllegalArgumentException("unexpected scheme " + feedUrl.getScheme() + " for url " + feedUrl);

    }

    private ExternalServices _es;
    private ExternalServices getExternalServices() {
        if (_es == null) {
            _es = new ExternalServicesBridgeFactory().getExternalServices();
        }
        return _es;
    }
    public void setKey(String key) {
        _key = key;
    }

    public void setConnectionManager(HttpClientConnectionManager connectionManager) {
        _connectionManager = connectionManager;
    }

    private URI getFeed(String feedId) throws URISyntaxException {
        String url = _feedToUrlMap.get(feedId);
        if (url == null) {
            URIBuilder ub = new URIBuilder(getFeed(feedId));
            ub.addParameter("key", _key);
            ub.addParameter("feed_id", feedId);
            return ub.build();
        }
        return new URIBuilder(url).build();
    }
}

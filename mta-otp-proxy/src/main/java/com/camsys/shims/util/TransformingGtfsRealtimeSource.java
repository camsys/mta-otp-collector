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
package com.camsys.shims.util;

import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.InputStream;

public class TransformingGtfsRealtimeSource implements GtfsRealtimeSource {

    private static final ExtensionRegistry _extensionRegistry;

    private static final Logger _log = LoggerFactory.getLogger(TransformingGtfsRealtimeSource.class);

    private FeedMessage _feedMessage;

    private GtfsRealtimeTransformer _transformer;

    private HttpClientConnectionManager _connectionManager;

    private CloseableHttpClient _httpClient;

    private int _nTries = 5;

    private int _retryDelay = 5;

    private String _sourceUrl;

    static {
        _extensionRegistry = ExtensionRegistry.newInstance();
        _extensionRegistry.add(GtfsRealtimeNYCT.nyctFeedHeader);
        _extensionRegistry.add(GtfsRealtimeNYCT.nyctTripDescriptor);
        _extensionRegistry.add(GtfsRealtimeNYCT.nyctStopTimeUpdate);
    }

    public void setConnectionManager(HttpClientConnectionManager connectionManager) {
        _connectionManager = connectionManager;
    }

    public void setNTries(int nTries) {
        _nTries = nTries;
    }

    public void setRetryDelay(int retryDelay) {
        _retryDelay = retryDelay;
    }

    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    public void setTransformer(GtfsRealtimeTransformer transformer) {
        _transformer = transformer;
    }

    public void update() {
        if (_httpClient == null)
            _httpClient = HttpClientBuilder.create().setConnectionManager(_connectionManager).build();

        HttpGet get = new HttpGet(_sourceUrl);
        get.setHeader("accept", "application/x-protobuf");

        FeedMessage message = null;
        for (int tries = 0; tries < _nTries; tries++) {
            try {
                CloseableHttpResponse response = _httpClient.execute(get);
                try (InputStream streamContent = response.getEntity().getContent()) {
                    message = FeedMessage.parseFrom(streamContent, _extensionRegistry);
                    if (!message.getEntityList().isEmpty())
                        break;
                    Thread.sleep(_retryDelay * 1000);
                }
            } catch (Exception e) {
                _log.error("Error parsing protocol feed: {}", _sourceUrl);
            }
        }

        _feedMessage = _transformer.transform(message);

    }

    public FeedMessage getFeed() {
        return _feedMessage;
    }

    public void addIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }

    public void removeIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }
}

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
package com.camsys.shims.util.source;

import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.InputStream;

/**
 * transform from type to source
 *
 */
public class TransformingGtfsRealtimeSource<T> implements UpdatingGtfsRealtimeSource {

    private static final Logger _log = LoggerFactory.getLogger(TransformingGtfsRealtimeSource.class);

    protected FeedMessage _feedMessage;

    protected GtfsRealtimeTransformer<T> _transformer;

    protected HttpClientConnectionManager _connectionManager;

    protected CloseableHttpClient _httpClient;

    protected Deserializer<T> _deserializer;

    protected int _nTries = 5;

    protected int _retryDelay = 5;

    protected String _sourceUrl;

    private String _overrideMimeType;

    /**
     * <p>setConnectionManager.</p>
     *
     * @param connectionManager a {@link org.apache.http.conn.HttpClientConnectionManager} object.
     */
    public void setConnectionManager(HttpClientConnectionManager connectionManager) {
        _connectionManager = connectionManager;
    }

    /**
     * <p>setNTries.</p>
     *
     * @param nTries a int.
     */
    public void setNTries(int nTries) {
        _nTries = nTries;
    }

    /**
     * <p>setRetryDelay.</p>
     *
     * @param retryDelay a int.
     */
    public void setRetryDelay(int retryDelay) {
        _retryDelay = retryDelay;
    }

    /**
     * <p>setSourceUrl.</p>
     *
     * @param sourceUrl a {@link java.lang.String} object.
     */
    public void setSourceUrl(String sourceUrl) {
        _sourceUrl = sourceUrl;
    }

    /**
     * <p>setTransformer.</p>
     *
     * @param transformer a {@link com.camsys.shims.util.transformer.GtfsRealtimeTransformer} object.
     */
    public void setTransformer(GtfsRealtimeTransformer transformer) {
        _transformer = transformer;
    }

    /**
     * <p>setDeserializer.</p>
     *
     * @param deserializer a {@link com.camsys.shims.util.deserializer.Deserializer} object.
     */
    public void setDeserializer(Deserializer<T> deserializer) {
        this._deserializer = deserializer;
    }

    /**
     * <p>setOverrideMimeType.</p>
     *
     * @param overrideMimeType a {@link java.lang.String} object.
     */
    public void setOverrideMimeType(String overrideMimeType) {
        _overrideMimeType = overrideMimeType;
    }

    /** {@inheritDoc} */
    @Override
    public void update() {
        T message = getMessage(_sourceUrl, _deserializer);

        if (message != null) {
            _feedMessage = _transformer.transform(message);
        }
    }

    /**
     * <p>getMessage.</p>
     *
     * @param sourceUrl a {@link java.lang.String} object.
     * @param deserializer a {@link com.camsys.shims.util.deserializer.Deserializer} object.
     * @return a T object.
     */
    public T getMessage(String sourceUrl, Deserializer<T> deserializer){
        if (_httpClient == null)
            _httpClient = HttpClientBuilder.create().setConnectionManager(_connectionManager).build();

        HttpGet get = new HttpGet(sourceUrl);
        get.setHeader("accept", _overrideMimeType == null ? deserializer.getMimeType() : _overrideMimeType);

        T message = null;
        for (int tries = 0; tries < _nTries; tries++) {
            try {
                CloseableHttpResponse response = _httpClient.execute(get);
                try (InputStream streamContent = response.getEntity().getContent()) {
                    message = deserializer.deserialize(streamContent);
                    if (message != null)
                        return message;
                    Thread.sleep(_retryDelay * 1000);
                }
            } catch (Exception e) {
                _log.error("Error parsing protocol feed: {}", sourceUrl);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * <p>getFeed.</p>
     *
     * @return a {@link com.google.transit.realtime.GtfsRealtime.FeedMessage} object.
     */
    public FeedMessage getFeed() {
        return _feedMessage;
    }

    /** {@inheritDoc} */
    public void addIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public void removeIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }

}

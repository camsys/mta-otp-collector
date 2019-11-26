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
import com.kurtraschke.nyctrtproxy.FeedManager;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.NotImplementedException;

import java.io.InputStream;

// transform from type to source
public class TransformingGtfsRealtimeSource<T> implements UpdatingGtfsRealtimeSource {

    private static final Logger _log = LoggerFactory.getLogger(TransformingGtfsRealtimeSource.class);

    private static final String feedId = "_default";

    protected FeedManager _feedManager;

    protected FeedMessage _feedMessage;

    protected GtfsRealtimeTransformer<T> _transformer;

    protected Deserializer<T> _deserializer;

    protected int _nTries = 5;

    protected int _retryDelay = 5;

    protected String _sourceUrl;

    public void setFeedManager(FeedManager feedManager) {
        _feedManager = feedManager;
    }
    public void setNTries(int nTries) {
        _nTries = nTries;
    }

    public void setRetryDelay(int retryDelay) {
        _retryDelay = retryDelay;
    }

    public void setTransformer(GtfsRealtimeTransformer transformer) {
        _transformer = transformer;
    }

    public void setDeserializer(Deserializer<T> deserializer) {
        this._deserializer = deserializer;
    }


    @Override
    public void update() {
        T message = getMessage(_deserializer);

        if (message != null) {
            _feedMessage = _transformer.transform(message);
        }
    }
    public T getMessage(Deserializer<T> deserializer){
        if (_feedManager == null) throw new IllegalStateException("_feedManager cannot be null for " + this.getClass().getName() + " and feed=" + feedId);
        String feedUrl = _feedManager.getFeedOrDefault(String.valueOf(feedId));
        return getMessage(feedUrl, deserializer);
    }


    public T getMessage(String feedUrl, Deserializer<T> deserializer){
        T message = null;
        for (int tries = 0; tries < _nTries; tries++) {
            try {
                try (InputStream streamContent = _feedManager.getStream(feedUrl, feedId)) {
                    try {
                        message = deserializer.deserialize(streamContent);
                    } catch (Throwable t) {
                        _log.error("fail for " + feedUrl);
                    }
                    if (message != null)
                        return message;
                    Thread.sleep(_retryDelay * 1000);
                }
            } catch (Exception e) {
                _log.error("Error parsing protocol feed: {}", feedUrl);
                e.printStackTrace();
            }
        }
        return null;
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

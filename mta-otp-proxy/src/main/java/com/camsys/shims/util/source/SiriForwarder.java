package com.camsys.shims.util.source;

import com.camsys.shims.gtfsrt.alerts.siri.deserializer.SiriDeserializer;

import com.camsys.shims.util.deserializer.Deserializer;
import com.kurtraschke.nyctrtproxy.FeedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.siri.siri.Siri;

import java.io.InputStream;

/**
 * Copy Siri input to Siri output without transformation so that it may be chained/merged
 * with other spring sources.
 */
public class SiriForwarder implements SiriSource {

    private final Logger _log = LoggerFactory.getLogger(SiriForwarder.class);

    private static final String feedId = "_default";

    private FeedManager _feedManager;
    private SiriDeserializer _deserializer;
    private Siri _siri;
    protected int _nTries = 5;

    protected int _retryDelay = 5;


    public void setFeedManager(FeedManager feedManager) {
        _feedManager = feedManager;
    }

    public void setDeserializer(SiriDeserializer deserializer) {
        _deserializer = deserializer;
    }

    public Siri transform(Siri obj) {
        _siri = obj;
        return obj;
    }

    @Override
    public void update() {
        try {
            Siri message = getMessage(_deserializer);

            if (message != null) {
                _siri = transform(message);
            }
        } catch (Throwable t) {
            _log.error("update failed:", t);
        }
    }

    public Siri getMessage(Deserializer deserializer){
        try {
            if (_feedManager == null)
                throw new IllegalStateException("_feedManager cannot be null for " + this.getClass().getName());
            String feedUrl = _feedManager.getFeedOrDefault(String.valueOf(feedId));
            return getMessage(feedUrl, deserializer);
        } catch (Throwable t) {
            _log.error("getMessage failed: ", t);
        }
        return null;
    }
    public Siri getMessage(String feedUrl, Deserializer deserializer){
        Siri message = null;
        for (int tries = 0; tries < _nTries; tries++) {
            try {
                try (InputStream streamContent = _feedManager.getStream(feedUrl, feedId)) {
                    try {
                        message = (Siri) deserializer.deserialize(streamContent);
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


    @Override
    public Siri getSiri() {
        return _siri;
    }
}

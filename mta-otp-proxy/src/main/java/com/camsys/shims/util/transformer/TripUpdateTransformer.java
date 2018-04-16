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
package com.camsys.shims.util.transformer;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.kurtraschke.nyctrtproxy.model.MatchMetrics;
import com.kurtraschke.nyctrtproxy.model.Status;
import com.kurtraschke.nyctrtproxy.services.ProxyDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TripUpdateTransformer implements GtfsRealtimeTransformer<FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(TripUpdateTransformer.class);

    private ProxyDataListener _cloudwatchService;

    private String _namespace;

    private String _feedId;

    public void setCloudwatchService(ProxyDataListener cloudwatchService) {
        _cloudwatchService = cloudwatchService;
    }

    public void setFeedId(String feedId) {
        _feedId = feedId;
    }

    public void setNamespace(String namespace) {
        _namespace = namespace;
    }

    @Override
    public FeedMessage transform(FeedMessage message) {
        FeedMessage.Builder builder = FeedMessage.newBuilder();
        builder.setHeader(message.getHeader());
        int nTotal = 0, nMatched = 0;
        MatchMetrics matchMetrics = new MatchMetrics();
        for (int i = 0; i < message.getEntityCount(); i++) {
            FeedEntity entity = message.getEntity(i);
            if (entity.hasTripUpdate()) {
                nTotal++;
                TripUpdate.Builder tu = transformTripUpdate(entity, matchMetrics);
                if (tu != null) {
                    FeedEntity.Builder feb = entity.toBuilder().setTripUpdate(tu);
                    builder.addEntity(feb);
                    nMatched++;
                    matchMetrics.addStatus(Status.STRICT_MATCH);
                }
            } else {
                builder.addEntity(entity);
            }
        }
        _log.debug("Matched {} / {} TripUpdates", nMatched, nTotal);
            matchMetrics.reportRecordsIn(nTotal);
            if (_cloudwatchService != null && _feedId != null && _namespace != null)
                _cloudwatchService.reportMatchesForTripUpdateFeed(_feedId, matchMetrics, _namespace);
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(FeedEntity fe, MatchMetrics matchMetrics);
}

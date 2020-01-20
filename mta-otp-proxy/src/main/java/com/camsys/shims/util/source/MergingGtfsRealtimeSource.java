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

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.apache.commons.lang.NotImplementedException;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MergingGtfsRealtimeSource implements UpdatingGtfsRealtimeSource {

    private static final Logger _log = LoggerFactory.getLogger(MergingGtfsRealtimeSource.class);
    private List<GtfsRealtimeSource> sources;

    private FeedMessage message;

    public MergingGtfsRealtimeSource(List<GtfsRealtimeSource> sources) {
        this.sources = sources;
    }

    @Override
    public FeedMessage getFeed() {
        return message;
    }

    @Override
    public void update() {
        List<GtfsRealtimeSource> successfulSources = new ArrayList<GtfsRealtimeSource>();
        for (GtfsRealtimeSource source : sources) {
            if (source instanceof UpdatingGtfsRealtimeSource) {
                try {
                    ((UpdatingGtfsRealtimeSource) source).update();
                    successfulSources.add(source);
                } catch (Throwable t) {
                    _log.error("update failed for source " + source, t);
                }
            }
        }
        FeedMessage.Builder message = FeedMessage.newBuilder();
        Set<String> ids = new HashSet<>();
        for (GtfsRealtimeSource source : successfulSources) {
            try {
                FeedMessage feed = source.getFeed();
                if (feed == null) {
                    continue;
                }
                if (!message.hasHeader()) {
                    message.setHeader(feed.getHeader());
                }
                if (feed.getEntityList() == null) {
                    continue;
                }
                for (FeedEntity entity : feed.getEntityList()) {
                    String id = entity.getId();
                    if (ids.contains(id)) {
                        id = id + " " + new Random().nextInt();
                        ids.add(id);
                        entity = entity.toBuilder().setId(id).build();
                    }
                    message.addEntity(entity);
                }
            } catch (Throwable tt) {
                _log.error("Exception:", tt);
        }
    }
        this.message = message.build();
    }

    @Override
    public void addIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }

    @Override
    public void removeIncrementalListener(GtfsRealtimeIncrementalListener listener) {
        throw new NotImplementedException();
    }

    public void setSources(List<GtfsRealtimeSource> sources) {
        this.sources = sources;
    }
}

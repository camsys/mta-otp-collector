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

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.apache.commons.lang.NotImplementedException;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MergingGtfsRealtimeSource implements UpdatingGtfsRealtimeSource {

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
        for (GtfsRealtimeSource source : sources) {
            if (source instanceof UpdatingGtfsRealtimeSource) {
                ((UpdatingGtfsRealtimeSource) source).update();
            }
        }
        FeedMessage.Builder message = FeedMessage.newBuilder();
        Set<String> ids = new HashSet<>();
        for (GtfsRealtimeSource source : sources) {
            FeedMessage feed = source.getFeed();
            if (!message.hasHeader()) {
                message.setHeader(feed.getHeader());
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

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

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import org.apache.commons.lang.NotImplementedException;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MergingGtfsRealtimeSource implements UpdatingGtfsRealtimeSource {

    private static final Logger _log = LoggerFactory.getLogger(MergingGtfsRealtimeSource.class);
    private List<GtfsRealtimeSource> sources;
    private Map<String, String> legacyAgencyIdMap = null;

    private FeedMessage message;

    public MergingGtfsRealtimeSource(List<GtfsRealtimeSource> sources) {
        this.sources = sources;
    }

    public void setLegacyAgencyIdMap(Map<String, String> map) {
        this.legacyAgencyIdMap = map;
    }

    @Override
    public FeedMessage getFeed() {
        return message;
    }

    @Override
    public void update() {
        try {
            FeedMessage.Builder message = FeedMessage.newBuilder();
            List<GtfsRealtimeSource> successfulSources = new ArrayList<GtfsRealtimeSource>();
            if (sources != null) {
                for (GtfsRealtimeSource source : sources) {
                    if (source != null) {
                        if (source instanceof UpdatingGtfsRealtimeSource) {
                            try {
                                ((UpdatingGtfsRealtimeSource) source).update();
                                successfulSources.add(source);
                            } catch (Throwable t) {
                                _log.error("update failed for source " + source, t);
                            }
                        }
                    }
                }
            }
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
                        // as we merge entities now is our chance to cleanup any legacy values
                        entity = cleanupEntity(entity);
                        message.addEntity(entity);
                    }
                } catch (Throwable tt) {
                    _log.error("Exception:", tt);
                }
            }
            try {
                if (!message.hasHeader() || !message.getHeader().hasTimestamp()) {
                    _log.error("missing header, adding new one");
                    GtfsRealtime.FeedHeader.Builder header = GtfsRealtime.FeedHeader.newBuilder();
                    header.setIncrementality(GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET);
                    header.setTimestamp(System.currentTimeMillis() / 1000);
                    header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
                    message.setHeader(header);
                }
                this.message = message.build();
            } catch (Throwable e) {
                _log.error("exception building message:", e);
            }
        } catch (Throwable ttt) {
            _log.error("general fault:", ttt);
        }
    }

    // mutate legacy values to something more public facing
    private FeedEntity cleanupEntity(FeedEntity entity) {
        // make sure we actually have work to do
        if (legacyAgencyIdMap == null) return entity;
        if (entity == null) return entity;
        if (!entity.hasAlert()) return entity;
        if (entity.getAlert().getInformedEntityList().isEmpty()) return entity;
        boolean modified = false;

        // iterate over the list of informed entities making a copy of each one as we go
        List<GtfsRealtime.EntitySelector> newElements = new ArrayList<>();
        for (GtfsRealtime.EntitySelector informedEntity : entity.getAlert().getInformedEntityList()) {
            if (informedEntity.hasAgencyId()) {
                if (legacyAgencyIdMap.containsKey(informedEntity.getAgencyId())) {
                    // mutate the copy to clean up legacy information
                    newElements.add(fixInformedEntity(informedEntity));
                    modified = true;
                } else {
                    // nothing to do in this case, just copy
                    newElements.add(informedEntity);
                }

            }
        }
        if (modified) {
            /*
             * if we are here, we successfully mutated an object and now need to
             * rebuild the alert and the entity list
             */
            GtfsRealtime.Alert.Builder newAlert = entity.getAlert().toBuilder();

            for (int i = newAlert.getInformedEntityCount()-1; i>=0; i--) {
                newAlert.removeInformedEntity(i);
            }

            for (GtfsRealtime.EntitySelector modifiedElements : newElements) {
                newAlert.addInformedEntity(modifiedElements);
            }
            return entity.toBuilder().setAlert(newAlert.build()).build();
        }
        return entity;
    }

    private GtfsRealtime.EntitySelector fixInformedEntity(GtfsRealtime.EntitySelector informedEntity) {
        String legacyAgencyId = informedEntity.getAgencyId();
        GtfsRealtime.EntitySelector.Builder builder = informedEntity.toBuilder();

        // first filter the agency
        builder.setAgencyId(fixAgency(legacyAgencyId));

        // next if the sort order references the legacy agency fix that as well
        if (builder.hasExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector)) {
            GtfsRealtimeServiceStatus.MercuryEntitySelector mercuryEntitySelector =
                    builder.getExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector);
            if (mercuryEntitySelector.hasSortOrder()) {
                builder.setExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector,
                        fixSortOrder(mercuryEntitySelector));
            }
        }
        return builder.build();
    }

    private GtfsRealtimeServiceStatus.MercuryEntitySelector fixSortOrder(GtfsRealtimeServiceStatus.MercuryEntitySelector selector) {
        GtfsRealtimeServiceStatus.MercuryEntitySelector.Builder builder = selector.toBuilder();
        builder.setSortOrder(fixSortOrderString(builder.getSortOrder()));
        return builder.build();
    }

    private String fixSortOrderString(String order) {
        if (legacyAgencyIdMap == null) return order;
        if (order == null) return order;
        for (String searchString : legacyAgencyIdMap.keySet()) {
            if (order.contains(searchString)) {
                return order.replaceAll(searchString, legacyAgencyIdMap.get(searchString));
            }
        }
        return order;
    }

    private String fixAgency(String agencyId) {
        if (legacyAgencyIdMap == null) return agencyId;
        if (!legacyAgencyIdMap.containsKey(agencyId)) return agencyId;
        return legacyAgencyIdMap.get(agencyId);
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

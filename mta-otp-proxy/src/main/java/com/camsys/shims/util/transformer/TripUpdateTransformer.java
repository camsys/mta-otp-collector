package com.camsys.shims.util.transformer;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedMessageOrBuilder;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class TripUpdateTransformer implements GtfsRealtimeTransformer<FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(TripUpdateTransformer.class);

    private ExternalServices externalServices = new ExternalServicesBridgeFactory().getExternalServices();

    private String _namespace;

    private String _feedId;

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
        List<FeedEntity> unmatchedEntities = new ArrayList<>();
        for (int i = 0; i < message.getEntityCount(); i++) {
            FeedEntity entity = message.getEntity(i);
            if (entity.hasTripUpdate()) {
                TripUpdate.Builder tu = transformTripUpdate(entity);
                if (tu != null) {
                    FeedEntity.Builder feb = entity.toBuilder().setTripUpdate(tu);
                    builder.addEntity(feb);
                } else {
                    unmatchedEntities.add(entity);
                }
            } else {
                builder.addEntity(entity);
            }
        }
        _log.info("Output {} entities", builder.getEntityCount());

        if (_feedId != null && _namespace != null) {
            publishMetrics(message, builder, unmatchedEntities);
        }
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(FeedEntity fe);

    public void publishMetrics(FeedMessageOrBuilder messageIn, FeedMessageOrBuilder messageOut, List<FeedEntity> unmatchedEntities) {
        long recordsIn = messageIn.getEntityList().stream().filter(FeedEntity::hasTripUpdate).count();
        int matchedTrips = 0, addedTrips = 0, recordsOut = 0;
        for (FeedEntity entity : messageOut.getEntityList()) {
            if (entity.hasTripUpdate()) {
                recordsOut++;
                if (entity.getTripUpdate().getTrip().getScheduleRelationship().equals(ScheduleRelationship.ADDED)) {
                    addedTrips++;
                } else {
                    matchedTrips++;
                }
            }
        }
        long timestamp = messageIn.getHeader().getTimestamp();
        long latency = (new Date().getTime()/1000) - timestamp;
        publishMetric("Latency", latency);
        publishMetric( "RecordsIn", recordsIn);
        publishMetric("AddedTrips", addedTrips);
        publishMetric("MatchedTrips", matchedTrips);
        publishMetric("RecordsOut", recordsOut);
        publishMetric("UnmatchedTrips", unmatchedEntities.size());
    }

    public void publishMetric(String name, double value) {
        if (externalServices.isInstancePrimary()) {
            externalServices.publishMetric(_namespace, name, "feed", _feedId, value);
        }
    }
}

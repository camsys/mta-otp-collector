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
        for (int i = 0; i < message.getEntityCount(); i++) {
            FeedEntity entity = message.getEntity(i);
            if (entity.hasTripUpdate()) {
                TripUpdate.Builder tu = transformTripUpdate(entity);
                if (tu != null) {
                    FeedEntity.Builder feb = entity.toBuilder().setTripUpdate(tu);
                    builder.addEntity(feb);
                }
            } else {
                builder.addEntity(entity);
            }
        }
        _log.info("Output {} entities", builder.getEntityCount());

        if (_feedId != null && _namespace != null) {
            publishMetrics(message, builder);
        }
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(FeedEntity fe);

    public void publishMetrics(FeedMessageOrBuilder messageIn, FeedMessageOrBuilder messageOut) {
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
        publishMetric( "RecordsIn", recordsIn);
        publishMetric("AddedTrips", addedTrips);
        publishMetric("MatchedTrips", matchedTrips);
        publishMetric("RecordsOut", recordsOut);

    }

    public void publishMetric(String name, double value) {
        if (externalServices.isInstancePrimary()) {
            externalServices.publishMetric(_namespace, name, "feed", _feedId, value);
        }
    }
}

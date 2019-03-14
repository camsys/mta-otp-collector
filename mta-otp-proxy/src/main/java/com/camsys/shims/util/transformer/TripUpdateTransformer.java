package com.camsys.shims.util.transformer;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.google.common.collect.Sets;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedMessageOrBuilder;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import com.kurtraschke.nyctrtproxy.services.ProxyDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

import static com.kurtraschke.nyctrtproxy.model.MatchMetrics.metricCount;

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

        if (_cloudwatchService != null && _feedId != null && _namespace != null) {
            Date timestamp = new Date();
            Dimension dim = new Dimension();
            dim.setName("feed");
            dim.setValue(_feedId);
            Set<MetricDatum> metrics = getMetrics(message, builder, timestamp, dim);
            _cloudwatchService.publishMetric(_namespace, metrics);
        }
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(FeedEntity fe);

    public Set<MetricDatum> getMetrics(FeedMessageOrBuilder messageIn, FeedMessageOrBuilder messageOut, Date timestamp, Dimension dim) {
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
        MetricDatum dRecordsIn = metricCount(timestamp, "RecordsIn", recordsIn, dim);
        MetricDatum dAdded = metricCount(timestamp, "AddedTrips", addedTrips, dim);
        MetricDatum dMatched = metricCount(timestamp, "MatchedTrips", matchedTrips, dim);
        MetricDatum dRecordsOut = metricCount(timestamp, "RecordsOut", recordsOut, dim);
        return Sets.newHashSet(dRecordsIn, dMatched, dAdded, dRecordsOut);
    }
}

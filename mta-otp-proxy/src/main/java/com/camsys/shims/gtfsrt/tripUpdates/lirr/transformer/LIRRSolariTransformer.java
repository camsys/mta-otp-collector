package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.gtfsrt.tripUpdates.lirr.service.LIRRSolariDataService;
import com.camsys.shims.gtfsrt.tripUpdates.lirr.service.StopTimeUpdateAddon;
import com.camsys.shims.gtfsrt.tripUpdates.lirr.service.StopTimeUpdateKey;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeMTARR;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Layer on Solari data to existing GTFS-RT TripUpdates.  If additional Solari updates remain
 * then create synthetic trips.
 */
public class LIRRSolariTransformer implements GtfsRealtimeTransformer<GtfsRealtime.FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(LIRRSolariTransformer.class);
    private LIRRSolariDataService _dataService;

    private String agencyId;

    public void setDataService(LIRRSolariDataService dataService) {
        this._dataService = dataService;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public GtfsRealtime.FeedMessage transform(GtfsRealtime.FeedMessage message) {

        Map<StopTimeUpdateKey, StopTimeUpdateAddon> externalUpdates = _dataService.getUpdateCache();
        GtfsRealtime.FeedMessage.Builder builder = GtfsRealtime.FeedMessage.newBuilder();
        builder.setHeader(message.getHeader());

        try {
            for (int i = 0; i < message.getEntityCount(); i++) {
                GtfsRealtime.FeedEntity entity = message.getEntity(i);
                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate.Builder tu = transformTripUpdate(externalUpdates, entity);
                    if (tu != null) {
                        GtfsRealtime.FeedEntity.Builder feb = entity.toBuilder().setTripUpdate(tu);
                        builder.addEntity(feb);
                    }
                } else {
                    builder.addEntity(entity);
                }
            }

            for (StopTimeUpdateAddon trainUpdate : externalUpdates.values()) {
                // synthetic trip to match signage
                GtfsRealtime.FeedEntity.Builder feb = createTripUpdateEntity(trainUpdate);
                if (feb != null) {
                    builder.addEntity(feb);
                }

            }
        } catch (Throwable t) {
            _log.error("transformation broke: {}", t, t);
        }
        return builder.build();
    }

    private GtfsRealtime.FeedEntity.Builder createTripUpdateEntity(StopTimeUpdateAddon trainUpdate) {

        GtfsRealtime.FeedEntity.Builder feb = GtfsRealtime.FeedEntity.newBuilder();
        GtfsRealtime.TripUpdate.Builder tu = GtfsRealtime.TripUpdate.newBuilder();
        // append something to make it obvious this is a synthetic update
        feb.setId(trainUpdate.getTripId().getId() + "_SIGN");

        GtfsRealtime.TripUpdate.StopTimeUpdate.Builder stub = GtfsRealtime.TripUpdate.StopTimeUpdate.newBuilder();
        stub.setStopId(trainUpdate.getStopId().getId());

        GtfsRealtime.TripDescriptor.Builder td = GtfsRealtime.TripDescriptor.newBuilder();
        td.setTripId(trainUpdate.getTripId().getId());

        // stop time event -> departure
        if (trainUpdate.getPredictedDeparture() > 0) {
            GtfsRealtime.TripUpdate.StopTimeEvent.Builder departure = GtfsRealtime.TripUpdate.StopTimeEvent.newBuilder();
            departure.setTime(trainUpdate.getPredictedDeparture());
            stub.setDeparture(departure);
        }


        // extensions
        GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate.Builder rrExt = GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate.newBuilder();
        if (trainUpdate.getTrack() != null && trainUpdate.getTrack().length() > 0)
            rrExt.setTrack(trainUpdate.getTrack());
        if (trainUpdate.getStatus() != null && trainUpdate.getStatus().length() > 0)
            rrExt.setTrainStatus(trainUpdate.getStatus());
        stub.setExtension(GtfsRealtimeMTARR.mtaRailroadStopTimeUpdate, rrExt.build());

        tu.setTrip(td);
        tu.addStopTimeUpdate(stub);
        feb.setTripUpdate(tu);
        return feb;
    }

    private GtfsRealtime.TripUpdate.Builder transformTripUpdate(Map<StopTimeUpdateKey, StopTimeUpdateAddon> externalUpdates, GtfsRealtime.FeedEntity fe) {
        if (!fe.hasTripUpdate()) return null;
        GtfsRealtime.TripUpdate.Builder tub = fe.getTripUpdate().toBuilder();
        String tripId = null;
        GtfsRealtime.TripUpdate tu = fe.getTripUpdate();
        if (tu.hasTrip() && tu.getTrip().hasTripId()) {
            tripId = tu.getTrip().getTripId();
        }

        for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : tub.getStopTimeUpdateList()) {
            GtfsRealtime.TripUpdate.StopTimeUpdate.Builder stub = stu.toBuilder();

            if (hasDataServiceUpdate(tripId, stu)) {
                updateFromSignage(externalUpdates, stub, tripId, stu);
            }
        }
        return tub;
    }

    private boolean hasDataServiceUpdate(String tripId, GtfsRealtime.TripUpdate.StopTimeUpdate stu) {
        if (_dataService == null) return false;
        if (!stu.hasStopId()) return false;
        StopTimeUpdateKey key = createKey(tripId, stu.getStopId());
        return _dataService.getUpdateCache().containsKey(key);
    }

    private StopTimeUpdateKey createKey(String tripId, String stopId) {
        AgencyAndId tripIdAndId = new AgencyAndId(agencyId, tripId);
        AgencyAndId stopIdAndId = new AgencyAndId(agencyId, stopId);
        return new StopTimeUpdateKey(tripIdAndId, stopIdAndId);
    }

    private void updateFromSignage(Map<StopTimeUpdateKey, StopTimeUpdateAddon> externalUpdates,
                                   GtfsRealtime.TripUpdate.StopTimeUpdate.Builder stub,
                                   String tripId, GtfsRealtime.TripUpdate.StopTimeUpdate stu) {

        StopTimeUpdateKey key = createKey(tripId, stu.getStopId());
        StopTimeUpdateAddon update = externalUpdates.remove(key);

        if (update == null) return;

        if (update.getTrack() != null) {
            GtfsRealtimeNYCT.NyctStopTimeUpdate.Builder nyctExt = GtfsRealtimeNYCT.NyctStopTimeUpdate.newBuilder();
            nyctExt.setActualTrack(update.getTrack());
            stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
        }

        GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate.Builder rrExt = GtfsRealtimeMTARR.MtaRailroadStopTimeUpdate.newBuilder();
        if (update.getTrack() != null)
            rrExt.setTrack(update.getTrack());
        if (update.getStatus() != null)
            rrExt.setTrainStatus(update.getStatus());
        stub.setExtension(GtfsRealtimeMTARR.mtaRailroadStopTimeUpdate, rrExt.build());

        if (update.getPredictedDeparture() > 0) {
            GtfsRealtime.TripUpdate.StopTimeEvent.Builder departure = GtfsRealtime.TripUpdate.StopTimeEvent.newBuilder();
            departure.setTime(update.getPredictedDeparture());
            stub.setDeparture(departure);
        }
    }
}

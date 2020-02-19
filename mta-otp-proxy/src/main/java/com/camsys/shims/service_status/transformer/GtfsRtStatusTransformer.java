package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.onebusaway.transit_data_federation.services.AgencyAndIdLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * build service status from GTFS-RT.
 */
public class GtfsRtStatusTransformer implements ServiceStatusTransformer<GtfsRealtime.FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(ServiceStatusTransformer.class);

    private static final String DEFAULT_AGENCY = "MTASBWY";

    private static final Pattern lmmRegex = Pattern.compile("lmm:\\d{13}:\\d*");

    @Override
    public List<RouteDetail> transform(GtfsRealtime.FeedMessage obj, String mode, GtfsDataService gtfsDataService,
                                       GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap) {
        ArrayList<RouteDetail> routeDetails = new ArrayList<>();
        int sortOrder = 0;
        if (obj == null) return routeDetails;
        for (GtfsRealtime.FeedEntity feedEntity : obj.getEntityList()) {
            if (feedEntity.hasAlert()) {
                GtfsRealtime.Alert alert = feedEntity.getAlert();
                GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert =  null;
                if (alert.hasExtension(GtfsRealtimeServiceStatus.mercuryAlert)) {
                    mercuryAlert =
                            alert.getExtension(GtfsRealtimeServiceStatus.mercuryAlert);
                }
                if (!validAlert(alert)) continue;
                routeDetails.add(getRouteDetailFromAlert(gtfsDataService, mode, feedEntity, mercuryAlert, sortOrder));
                sortOrder++;
            }
        }
        _log.info("returning " + routeDetails + " route detail objects");
        return routeDetails;
    }

    private RouteDetail getRouteDetailFromAlert(GtfsDataService gtfsDataService, String mode, GtfsRealtime.FeedEntity entity,
                                                GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert, int sortOrder) {
        RouteDetail rd = new RouteDetail();
        if (mercuryAlert != null) {
            if (mercuryAlert.hasUpdatedAt())
                rd.setLastUpdated(new Date(mercuryAlert.getUpdatedAt()));
        }
        rd.setRouteSortOrder(sortOrder);
        rd.setInService(true);
        rd.setStatusDetails(new HashSet<StatusDetail>());
        for (GtfsRealtime.EntitySelector informedEntity : entity.getAlert().getInformedEntityList()) {
            rd.setAgency(informedEntity.getAgencyId());
            if (informedEntity.getAgencyId() == null || informedEntity.getAgencyId().length() == 0) {
                rd.setAgency("MTASBWY");
            }

            if (informedEntity.hasRouteId()) {
                if (informedEntity.hasAgencyId() && informedEntity.getRouteId().length() > 0) {
                    rd.setRouteId(new AgencyAndId(informedEntity.getAgencyId(), informedEntity.getRouteId()).toString());
                } else {
                    rd.setRouteId(new AgencyAndId(DEFAULT_AGENCY, informedEntity.getRouteId()).toString());
                }
            }

            if (informedEntity.hasTrip()) {
                GtfsRealtime.TripDescriptor tripDescriptor = informedEntity.getTrip();
                if (tripDescriptor.hasRouteId()) {
                    if (informedEntity.hasAgencyId() && informedEntity.getRouteId().length() > 0) {
                        rd.setRouteId(new AgencyAndId(informedEntity.getAgencyId(), tripDescriptor.getRouteId()).toString());
                    } else {
                        rd.setRouteId(new AgencyAndId(DEFAULT_AGENCY, tripDescriptor.getRouteId()).toString());
                    }
                }
            }
            rd.getStatusDetails().addAll(getStatusDetailFromAlert(entity, mercuryAlert));
            // set last updated so filtering works
            if (!rd.getStatusDetails().isEmpty() && rd.getLastUpdated() == null) {
                // make sure we have a last updated or filtering will fail
                rd.setLastUpdated(rd.getStatusDetails().iterator().next().getCreationDate());
            }
            rd.setMode(mode);
            Route route = gtfsDataService.getRouteForId(AgencyAndIdLibrary.convertFromString(rd.getRouteId()));
            if (route == null) {
                for (Route aRoute : gtfsDataService.getAllRoutes()) {
                    if (rd.getRouteId().equals(aRoute.getId().getId())) {
                        _log.error("route not found, but exists as " + aRoute.getId());
                    }
                }
                rd.setColor("black");
                rd.setRouteName(rd.getRouteId());
                rd.setRouteType(1);
                _log.warn("could not find route = " + new AgencyAndId("MTASBWY", rd.getRouteId()));
            } else {
                rd.setColor(route.getColor());
                rd.setRouteType(route.getType());
                rd.setRouteName(route.getShortName());
            }

        }
        return rd;
    }

    private List<StatusDetail> getStatusDetailFromAlert(GtfsRealtime.FeedEntity entity, GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert) {
        GtfsRealtime.Alert alert = entity.getAlert();
        List<StatusDetail> statusDetails = new ArrayList<>();

        StatusDetail sd1 = new StatusDetail();
        StatusDetail sd2 = new StatusDetail();

        if (mercuryAlert != null && mercuryAlert.hasCreatedAt()) {
            // if we have an explicit creation date prefer that
            sd1.setCreationDate(new Date(mercuryAlert.getCreatedAt()));
            sd2.setCreationDate(new Date(mercuryAlert.getCreatedAt()));
        }
        else if (entity.hasId()) {
            sd1.setId(entity.getId());
            sd2.setId(entity.getId());

            if (sd1.getCreationDate() == null) {
                if (dateEncodedid(entity.getId())) {
                    // id has date epoch encoded in it per format:  lmm:<epoch creation date in milliseconds>:<entity_id>
                    sd1.setCreationDate(getDateFromId(entity.getId()));
                    sd2.setCreationDate(getDateFromId(entity.getId()));
                } else {
                // otherwise store the id as is, and set creation date to be now
                    sd1.setCreationDate(new Date());
                    sd2.setCreationDate(new Date());
                }
;            }
        }
        statusDetails.add(sd1);
        if (mercuryAlert != null && mercuryAlert.hasAlertType())
            sd1.setStatusSummary(mercuryAlert.getAlertType());
        else
            sd1.setStatusSummary(alert.getHeaderText().getTranslation(0).getText());
        sd1.setStatusDescription(alert.getDescriptionText().getTranslation(0).getText());
        sd1.setDirection("0");

        for (GtfsRealtime.EntitySelector entitySelector : alert.getInformedEntityList()) {
                if (entitySelector.hasExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector)) {
                    GtfsRealtimeServiceStatus.MercuryEntitySelector mercuryEntitySelector =
                    entitySelector.getExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector);
                    if (mercuryEntitySelector.hasSortOrder()) {
                        sd1.setPriority(parseSortOrder(mercuryEntitySelector.getSortOrder()));
                        sd2.setPriority(sd1.getPriority());
                    }
                }
        }
        if (sd1.getPriority() == null) {
            sd1.setPriority(BigInteger.valueOf(6));
            sd2.setPriority(BigInteger.valueOf(6));
        }
        sd2.setDirection("1");
        statusDetails.add(sd2);
        sd2.setStatusSummary(sd1.getStatusSummary());
        sd2.setStatusDescription(sd1.getStatusDescription());
        return statusDetails;

    }

    private BigInteger parseSortOrder(String sortOrder) {
        // from GtfsRealtimeServiceStatus: expect format of  "GTFS-ID:Priority"
        // Priority maps to GtfsRealtimeServiceStatus.Priority
        int pos = sortOrder.lastIndexOf(":");
        if (pos > 0)
            try {
                return BigInteger.valueOf(Integer.parseInt(sortOrder.substring(pos + 1)));
            } catch (NumberFormatException nfe) {
                _log.error("invalid sortOrder |" + sortOrder + "|");
                return BigInteger.valueOf(6);
            }
        _log.error("unexpected sortOrder |" + sortOrder + "|");
        return BigInteger.valueOf(6);

    }

    private boolean dateEncodedid(String id) {
        return lmmRegex.matcher(id).matches();
    }

    private Date getDateFromId(String id) {
        int start = id.indexOf(':');
        int end = id.indexOf(':', start+1);
        try {
            long millis = Long.parseLong(id.substring(start + 1, end));
            return new Date(millis);
        } catch (NumberFormatException nfe) {
            _log.error("unexpected date epoch=" + id.substring(start + 1, end) + " from id=" + id);
            return new Date();
        }
    }

    private boolean validAlert(GtfsRealtime.Alert alert) {
        if (alert.getInformedEntityList().isEmpty()) return false;
        if (!alert.getInformedEntity(0).hasRouteId()) return false;
        if (alert.getHeaderText() == null) return false;
        if (alert.getDescriptionText() == null) return false;
        return true;
    }

}

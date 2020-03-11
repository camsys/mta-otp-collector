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
                routeDetails.addAll(getRouteDetailFromAlert(gtfsDataService, mode, feedEntity, mercuryAlert, sortOrder));
                sortOrder++;
            }
        }
        _log.info("returning " + routeDetails + " route detail objects");
        return routeDetails;
    }

    private List<RouteDetail> getRouteDetailFromAlert(GtfsDataService gtfsDataService, String mode, GtfsRealtime.FeedEntity entity,
                                                GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert, int sortOrder) {

        List<RouteDetail> routeDetails = new ArrayList<RouteDetail>();

        for (GtfsRealtime.EntitySelector informedEntity : entity.getAlert().getInformedEntityList()) {
            RouteDetail rd = new RouteDetail();
            routeDetails.add(rd);
            if (mercuryAlert != null) {
                if (mercuryAlert.hasUpdatedAt())
                    rd.setLastUpdated(new Date(mercuryAlert.getUpdatedAt()*1000));
            }
            rd.setRouteSortOrder(sortOrder);
            rd.setInService(true);
            rd.setStatusDetails(new HashSet<StatusDetail>());

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
                    if (rd.getRouteId() != null) {
                        if (rd.getRouteId().equals(aRoute.getId().getId())) {
                            _log.error("route not found, but exists as " + aRoute.getId());
                        }
                    } else {
                        _log.error("no route id for rd=" + rd);
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
        return routeDetails;
    }

    private List<StatusDetail> getStatusDetailFromAlert(GtfsRealtime.FeedEntity entity, GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert) {
        GtfsRealtime.Alert alert = entity.getAlert();
        List<StatusDetail> statusDetails = new ArrayList<>();
        boolean foundDirection = false;

        for (GtfsRealtime.EntitySelector entitySelector : alert.getInformedEntityList()) {
            StatusDetail sdx = new StatusDetail();
            statusDetails.add(sdx);
            sdx.setId(entity.getId());
            sdx.setStatusDescription(getDescriptionTranslation(alert));

            // consume the extension if present
            if (entitySelector.hasExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector)) {
                GtfsRealtimeServiceStatus.MercuryEntitySelector mercuryEntitySelector =
                        entitySelector.getExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector);
                if (mercuryEntitySelector.hasSortOrder()) {
                    sdx.setPriority(parseSortOrder(mercuryEntitySelector.getSortOrder()));
                }
            }
            // ensure all fields have some reasonable defaults

            if (mercuryAlert != null) {
                if (mercuryAlert.hasCreatedAt()) {
                    // if we have an explicit creation date prefer that
                    sdx.setCreationDate(new Date(mercuryAlert.getCreatedAt()*1000));
                }
                if (mercuryAlert.hasAlertType()) {
                    sdx.setStatusSummary(mercuryAlert.getAlertType());
                }
            }

            if (sdx.getCreationDate() == null) {
                if (dateEncodedid(entity.getId())) {
                    // id has date epoch encoded in it per format:  lmm:<epoch creation date in milliseconds>:<entity_id>
                    sdx.setCreationDate(getDateFromId(entity.getId()));
                } else {
                    sdx.setCreationDate(new Date());
                }
            }

            // start - end date from active period
            if (alert.getActivePeriodCount() > 0) {
                // we assume first record only
                GtfsRealtime.TimeRange activePeriod = alert.getActivePeriod(0);
                if (activePeriod.hasStart())
                    sdx.setStartDate(new Date(activePeriod.getStart()*1000));
                if (activePeriod.hasEnd())
                    sdx.setEndDate(new Date(activePeriod.getEnd()*1000));
            } else {
                // here we default the start date to the creation date so that its always present
                sdx.setStartDate(sdx.getCreationDate());
            }

            if (sdx.getStatusSummary() == null) {
                sdx.setStatusSummary(getHeaderTranslation(alert));
            }

            if (sdx.getPriority() == null) {
                sdx.setPriority(BigInteger.valueOf(6));
            }

            if (getDirection(alert) != null) {
                foundDirection = true;
                sdx.setDirection(getDirection(alert));
            }

            if (sdx.getDirection() == null) {
                StatusDetail sd2 = createReverseDirection(sdx);
                statusDetails.add(sd2);
                sd2.setDirection("1");
                sdx.setDirection("0");
                statusDetails.add(sd2);
            }

        } // end entity list loop

        return statusDetails;

    }

    private String getDescriptionTranslation(GtfsRealtime.Alert alert) {
        if (alert.hasDescriptionText()) {
            if (alert.getDescriptionText().getTranslationList() != null
                    && !alert.getDescriptionText().getTranslationList().isEmpty()) {
                return alert.getDescriptionText().getTranslation(0).getText();
            }
        }
        return "";
    }
    private String getHeaderTranslation(GtfsRealtime.Alert alert) {
        if (alert.hasHeaderText()) {
            if (alert.getHeaderText().getTranslationList() != null
                    && !alert.getHeaderText().getTranslationList().isEmpty()) {
                return alert.getHeaderText().getTranslation(0).getText();
            }
        }
        return "";
    }

    private String getDirection(GtfsRealtime.Alert alert) {
        if (alert.getInformedEntityList().isEmpty()) return null;
        if (!alert.getInformedEntity(0).hasTrip()) return null;
        if (alert.getInformedEntity(0).getTrip().hasDirectionId()) return null;
        return String.valueOf(alert.getInformedEntity(0).getTrip().getDirectionId());
    }

    private StatusDetail createReverseDirection(StatusDetail sd1) {
        StatusDetail sd2 = new StatusDetail();
        sd2.setCreationDate(sd1.getCreationDate());
        sd2.setId(sd1.getId());
        sd2.setPriority(sd1.getPriority());
        sd2.setStatusSummary(sd1.getStatusSummary());
        sd2.setStatusDescription(sd1.getStatusDescription());
        sd2.setStartDate(sd1.getStartDate());
        sd2.setEndDate(sd1.getEndDate());
        return sd2;
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

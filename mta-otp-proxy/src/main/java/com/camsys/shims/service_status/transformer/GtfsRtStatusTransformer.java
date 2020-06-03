package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.services.GtfsDataService;

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

    private static final Logger _log = LoggerFactory.getLogger(GtfsRtStatusTransformer.class);

    private static final Pattern lmmRegex = Pattern.compile("lmm:\\d{13}:\\d*");

    private String preferredTranslation = "en-html";
    private String fallbackTranslation = "en";
    private Map<String, String> legacyAgencyIdMap = null;
    // special cases for treatment of owning agency
    private List<String> rebrandedService = null;

    public void setPreferredTranslation(String tr) {
        this.preferredTranslation = tr;
    }

    public void setLegacyAgencyIdMap(Map<String, String> map) {
        this.legacyAgencyIdMap = map;
    }

    public void setFallbackTranslation(String tr) {
        this.fallbackTranslation = tr;
    }

    public void setRebrandedService(List<String> service) {
        if (service != null) {
            rebrandedService = service;
        }
    }

    @Override
    public List<RouteDetail> transform(GtfsRealtime.FeedMessage obj, String mode, List<GtfsDataService> gtfsDataServices,
                                       GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap) {
        ArrayList<RouteDetail> routeDetails = new ArrayList<>();
            int sortOrder = 0;
            if (obj == null) return routeDetails;
            for (GtfsRealtime.FeedEntity feedEntity : obj.getEntityList()) {
                if (feedEntity.hasAlert()) {
                    GtfsRealtime.Alert alert = feedEntity.getAlert();
                    GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert = null;
                    if (alert.hasExtension(GtfsRealtimeServiceStatus.mercuryAlert)) {
                        mercuryAlert =
                                alert.getExtension(GtfsRealtimeServiceStatus.mercuryAlert);
                    }
                    if (!validAlert(alert)) continue;
                    routeDetails.addAll(getRouteDetailFromAlert(gtfsDataServices, mode, feedEntity, mercuryAlert, sortOrder));
                    sortOrder++;
                }
            }
        _log.debug("returning " + routeDetails + " route detail objects");
        return routeDetails;
    }

    private List<RouteDetail> getRouteDetailFromAlert(List<GtfsDataService> gtfsDataServices, String mode, GtfsRealtime.FeedEntity entity,
                                                GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert, int sortOrder) {

        List<RouteDetail> routeDetails = new ArrayList<RouteDetail>();

        for (GtfsRealtime.EntitySelector informedEntity : entity.getAlert().getInformedEntityList()) {
            RouteDetail rd = new RouteDetail();
            if (informedEntity.hasAgencyId() && informedEntity.getRouteId().length() > 0) {
                int delimiter = informedEntity.getRouteId().indexOf(":");
                if (isRebrandedService(informedEntity.getRouteId()) && delimiter > 0) {
                    rd.setRouteId(informedEntity.getRouteId().substring(0, delimiter)
                                    + "_"
                                    +  informedEntity.getRouteId().substring(delimiter+1, informedEntity.getRouteId().length()));
                    _log.info("remapped routeId " + informedEntity.getRouteId() + " to " + rd.getRouteId());
                } else {
                    rd.setRouteId(new AgencyAndId(filterAgency(informedEntity.getAgencyId()), informedEntity.getRouteId()).toString());
                }
            } else {
                _log.debug("discarding non-service status alert for " + informedEntity.toString());
                continue;
            }

            Route route = getRouteForId(gtfsDataServices, informedEntity);
            if (route == null) {
                _log.error("illegal route for entity " + entity.getAlert());
                continue;
            } else {
                // only continue if we've matched to a GTFS route
                routeDetails.add(rd);
                rd.setColor(route.getColor());
                rd.setRouteType(route.getType());
                if (isRebrandedService(informedEntity.getRouteId())) {
                    rd.setRouteName(route.getLongName());
                    _log.info("rebranding " + rd.getRouteId() + " as " + rd.getRouteName());
                } else {
                    rd.setRouteName(route.getShortName());
                }
            }

            if (mercuryAlert != null) {
                if (mercuryAlert.hasUpdatedAt())
                    rd.setLastUpdated(new Date(mercuryAlert.getUpdatedAt() * 1000));
            }
            rd.setRouteSortOrder(sortOrder);
            rd.setInService(true);
            rd.setStatusDetails(new HashSet<StatusDetail>());

            rd.setAgency(filterAgency(informedEntity.getAgencyId()));

            if (informedEntity.hasTrip()) {
                GtfsRealtime.TripDescriptor tripDescriptor = informedEntity.getTrip();
                if (tripDescriptor.hasRouteId()) {
                    if (informedEntity.hasAgencyId() && informedEntity.getRouteId().length() > 0) {
                        rd.setRouteId(new AgencyAndId(filterAgency(informedEntity.getAgencyId()), tripDescriptor.getRouteId()).toString());
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

        }
        return routeDetails;
    }

    private boolean isRebrandedService(String routeId) {
        if (rebrandedService == null || rebrandedService.isEmpty()) return false;
        for (String s : rebrandedService) {
            if (s != null && s.equals(routeId)) return true;
        }
        return false;
    }

    private Route getRouteForId(List<GtfsDataService> gtfsDataServices, GtfsRealtime.EntitySelector informedEntity) {
        for (GtfsDataService gtfsDataService : gtfsDataServices) {
            Route route = gtfsDataService.getRouteForId(new AgencyAndId(informedEntity.getAgencyId(), informedEntity.getRouteId()));
            if (route != null) return route;
            // try non-legacy agency id
            route = gtfsDataService.getRouteForId(new AgencyAndId(filterAgency(informedEntity.getAgencyId()), informedEntity.getRouteId()));
            if (route != null) return route;
            // try embedded agency:route_id as with MNR -> NJT:6 and MNR -> NJT:13
            int delimiter = informedEntity.getRouteId().indexOf(":");
            if (isRebrandedService(informedEntity.getRouteId()) && delimiter > 0) {
                String agencyId = informedEntity.getRouteId().substring(0, delimiter);
                String routeId = informedEntity.getRouteId().substring(delimiter+1, informedEntity.getRouteId().length());
                AgencyAndId routeAndId = new AgencyAndId(agencyId, routeId);
                route = gtfsDataService.getRouteForId(routeAndId);

                if (route != null) {
                    // we need to cleanup the route for this special case
                    Agency a = new Agency();
                    a.setId(agencyId);
                    route.setAgency(a);
                    route.setId(routeAndId);
                    return route;
                }
            }
        }
        _log.info("route not found " + informedEntity.getAgencyId() + ":" + informedEntity.getRouteId());
        return null;
    }

    private String filterAgency(String agencyId) {
        if (legacyAgencyIdMap == null) return agencyId;
        if (!legacyAgencyIdMap.containsKey(agencyId)) return agencyId;
        return legacyAgencyIdMap.get(agencyId);
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
                if (alert.getDescriptionText().getTranslationList().size() == 1) {
                    return alert.getDescriptionText().getTranslation(0).getText();
                } else {
                    // we have multiple elements, select the most appropriate
                    return findHtmlTranslation(alert.getDescriptionText().getTranslationList());
                }
            }
        }
        return "";
    }
    private String getHeaderTranslation(GtfsRealtime.Alert alert) {
        if (alert.hasHeaderText()) {
            if (alert.getHeaderText().getTranslationList() != null
                    && !alert.getHeaderText().getTranslationList().isEmpty()) {

                if (alert.getHeaderText().getTranslationList().size() == 1) {
                    return alert.getHeaderText().getTranslation(0).getText();
                } else {
                    return findHtmlTranslation(alert.getHeaderText().getTranslationList());
                }
            }
        }
        return "";
    }

    private String findHtmlTranslation(List<GtfsRealtime.TranslatedString.Translation> translationList) {
        // look for the html variant first
        for (GtfsRealtime.TranslatedString.Translation t : translationList) {
            if (preferredTranslation.equalsIgnoreCase(t.getLanguage())) {
                return t.getText();
            }
        }
        // failing an html variant, look for english
        for (GtfsRealtime.TranslatedString.Translation t : translationList) {
            if (fallbackTranslation.equalsIgnoreCase(t.getLanguage())) {
                return t.getText();
            }
        }

        // use the first thing we find
        _log.info("unmatched translation type, using " + translationList.get(0).getLanguage());
        return translationList.get(0).getText();
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
        return new MercuryUtils().parseSortOrder(sortOrder);
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

package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;

import com.google.transit.realtime.GtfsRealtime;
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

/**
 * build service status from GTFS-RT.
 */
public class GtfsRtStatusTransformer implements ServiceStatusTransformer<GtfsRealtime.FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(ServiceStatusTransformer.class);

    private static final String DEFAULT_AGENCY = "MTASBWY";
    @Override
    public List<RouteDetail> transform(GtfsRealtime.FeedMessage obj, String mode, GtfsDataService gtfsDataService,
                                       GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap) {
        ArrayList<RouteDetail> routeDetails = new ArrayList<>();
        int sortOrder = 0;
        if (obj == null) return routeDetails;
        for (GtfsRealtime.FeedEntity feedEntity : obj.getEntityList()) {
            if (feedEntity.hasAlert()) {
                GtfsRealtime.Alert alert = feedEntity.getAlert();
                if (!validAlert(alert)) continue;
                routeDetails.add(getRouteDetailFromAlert(gtfsDataService, mode, alert, sortOrder));
                sortOrder++;
            }
        }
        _log.info("returning " + routeDetails + " route detail objects");
        return routeDetails;
    }

    private RouteDetail getRouteDetailFromAlert(GtfsDataService gtfsDataService, String mode, GtfsRealtime.Alert alert, int sortOrder) {
        RouteDetail rd = new RouteDetail();
        rd.setRouteSortOrder(sortOrder);
        rd.setInService(true);
        rd.setStatusDetails(new HashSet<StatusDetail>());
        for (GtfsRealtime.EntitySelector informedEntity : alert.getInformedEntityList()) {
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
            rd.getStatusDetails().addAll(getStatusDetailFromAlert(alert));
            rd.setMode(mode);
            Route route = gtfsDataService.getRouteForId(new AgencyAndId("MTASBWY", rd.getRouteId()));
            if (route == null) {
                _log.warn("could not find route = " + new AgencyAndId("MTASBWY", rd.getRouteId()));
            } else {
                rd.setColor(route.getColor());
                rd.setRouteType(route.getType());
            }

        }
        return rd;
    }

    private List<StatusDetail> getStatusDetailFromAlert(GtfsRealtime.Alert alert) {
        List<StatusDetail> statusDetails = new ArrayList<>();

        StatusDetail sd1 = new StatusDetail();
        statusDetails.add(sd1);
        sd1.setStatusSummary(alert.getHeaderText().getTranslation(0).getText());
        sd1.setStatusDescription(alert.getDescriptionText().getTranslation(0).getText());
        sd1.setDirection("0");
        sd1.setPriority(BigInteger.valueOf(6));
        sd1.setCreationDate(new Date());
        // TODO provide real dates
        sd1.setStartDate(new Date(System.currentTimeMillis()-12*24*60*60*1000));
        sd1.setEndDate(new Date(System.currentTimeMillis()+12*24*60*60*1000));
        // TODO check feed for this info
        StatusDetail sd2 = new StatusDetail();
        sd2.setPriority(BigInteger.valueOf(6));
        sd2.setCreationDate(new Date());
        sd2.setStartDate(new Date(System.currentTimeMillis()-12*24*60*60*1000));
        sd2.setEndDate(new Date(System.currentTimeMillis()+12*24*60*60*1000));
        sd2.setDirection("1");
        statusDetails.add(sd2);
        sd2.setStatusSummary(sd1.getStatusSummary());
        sd2.setStatusDescription(sd1.getStatusDescription());
        return statusDetails;

    }
    private boolean validAlert(GtfsRealtime.Alert alert) {
        if (alert.getInformedEntityList().isEmpty()) return false;
        if (!alert.getInformedEntity(0).hasRouteId()) return false;
        if (alert.getHeaderText() == null) return false;
        if (alert.getDescriptionText() == null) return false;
        return true;
    }

}

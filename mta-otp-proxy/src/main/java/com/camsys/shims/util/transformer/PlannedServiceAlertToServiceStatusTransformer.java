package com.camsys.shims.util.transformer;

import com.camsys.mta.plannedwork.Getstatus4ResponseType;
import com.camsys.mta.plannedwork.RouteinfoType;
import com.camsys.mta.plannedwork.StatusType;
import com.camsys.shims.atis.AtisGtfsMap;
import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.Alert;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.EntitySelector;
import com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder;
import com.google.transit.realtime.GtfsRealtimeConstants;

import com.google.transit.realtime.GtfsRealtimeOneBusAway;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.jsoup.Jsoup;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>PlannedServiceAlertToServiceStatusTransformer class.</p>
 *
 */
public class PlannedServiceAlertToServiceStatusTransformer implements GtfsRealtimeTransformer<Getstatus4ResponseType> {

    private GtfsRouteAdapter _gtfsRouteAdapter;
    private static Logger _log = LoggerFactory.getLogger(PlannedServiceAlertToServiceStatusTransformer.class);

    private boolean _addPlannedServiceAlerts = true;
    private boolean _includeDirecton = false;

    private String _agencyId = "";

    private AtisGtfsMap _atisGtfsMap;

    /**
     * <p>getIncludeDirecton.</p>
     *
     * @return a boolean.
     */
    public boolean getIncludeDirecton() {
        return _includeDirecton;
    }
    /**
     * <p>setIncludeDirecton.</p>
     *
     * @param includeDirecton a boolean.
     */
    public void setIncludeDirecton(boolean includeDirecton) {
        this._includeDirecton = _includeDirecton;
    }

    /**
     * <p>getAgencyId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAgencyId(){ return _agencyId; }
    /**
     * <p>setAgencyId.</p>
     *
     * @param agencyId a {@link java.lang.String} object.
     */
    public void setAgencyId(String agencyId){ _agencyId = agencyId; }

    /**
     * <p>getAtisGtfsMap.</p>
     *
     * @return a {@link com.camsys.shims.atis.AtisGtfsMap} object.
     */
    public AtisGtfsMap getAtisGtfsMap() { return _atisGtfsMap; }
    /**
     * <p>setAtisGtfsMap.</p>
     *
     * @param atisGtfsMap a {@link com.camsys.shims.atis.AtisGtfsMap} object.
     */
    public void setAtisGtfsMap(AtisGtfsMap atisGtfsMap) {
        _atisGtfsMap = atisGtfsMap;
    }

    /**
     * <p>getAddPlannedServiceAlerts.</p>
     *
     * @return a boolean.
     */
    public boolean getAddPlannedServiceAlerts() { return _addPlannedServiceAlerts; }
    /**
     * <p>setAddPlannedServiceAlerts.</p>
     *
     * @param _addPlannedServiceAlerts a boolean.
     */
    public void setAddPlannedServiceAlerts(boolean _addPlannedServiceAlerts) {
        this._addPlannedServiceAlerts = _addPlannedServiceAlerts;
    }


    /**
     *  Two builders are here. The first is a standard intantiator. The second is part of an experiment to see
     *  if we can make this configurable or extensible because the alerts received are going to be specific to the
     *  the agency this is pulled for.
     */
    public PlannedServiceAlertToServiceStatusTransformer() {}


    /** {@inheritDoc} */
    @Override
    public FeedMessage transform(Getstatus4ResponseType statusResponseType) {
        FeedMessage.Builder message = FeedMessage.newBuilder();
        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        if(_addPlannedServiceAlerts)
        {
            for (StatusType statusType : statusResponseType.getStatuses().getStatus()) {
                List<RouteinfoType> routes = findRouteinfoTypeFromStatus(statusType.getId(), statusResponseType.getRoutes().getRouteinfo());
                if(routes.size() > 0)
                {
                    try{
                        AgencyAndId agencyFromRouteId = _atisGtfsMap.getAgencyAndIdFromAtisIdWithoutAgency(routes.get(0).getRoute());
                        if(agencyFromRouteId != null )
                        {
                            if(agencyFromRouteId.getAgencyId().equals(_agencyId))
                            {
                                FeedEntity fe =  statusTypeToEntity(statusType, routes);
                                message.addEntity(fe);
                            }
                        }else{
                            _log.error("Failed to find agency from route " + routes.get(0).getRoute());
                        }
                    }catch (Exception e){
                        _log.error("Failed to convert the alert to GTFS for  " + e);
                    }

                }
            }
        }else{

        }

        return message.build();
    }

    private FeedEntity statusTypeToEntity(StatusType statusType, List<RouteinfoType> routeInfoTypeFromStatuses) {
        Alert.Builder alert = Alert.newBuilder();

        String desc = statusType.getTexts().getText();

        alert.setHeaderText(translatedString(statusType.getTitle()));
        alert.setDescriptionText(translatedString(Jsoup.parse(desc).text()));

        for (RouteinfoType route : routeInfoTypeFromStatuses) {
            alert.addInformedEntity(routeToInformedEntity(statusType.getId(), route));
        }

        //TODO this could be safer as regexp parsing
        if (!statusType.getBegins().toUpperCase().trim().equals("NONE") && !statusType.getExpires().toUpperCase().trim().equals("NONE")) {
            alert.addActivePeriod(GtfsRealtime.TimeRange.newBuilder()
                    .setStart(StatusTypeBeginStringToDateTime(statusType.getBegins()))
                    .setEnd(StatusTypeExpiresStringToDateTime(statusType.getExpires()))
            );
        }else if(!statusType.getBegins().toUpperCase().trim().equals("NONE") && statusType.getExpires().toUpperCase().trim().equals("NONE"))
        {
            alert.addActivePeriod(GtfsRealtime.TimeRange.newBuilder()
                    .setStart(StatusTypeBeginStringToDateTime(statusType.getBegins()))
            );
        }else if (statusType.getBegins().toUpperCase().trim().equals("NONE") && !statusType.getExpires().toUpperCase().trim().equals("NONE"))
        {
            alert.addActivePeriod(GtfsRealtime.TimeRange.newBuilder()
                    .setEnd(StatusTypeExpiresStringToDateTime(statusType.getExpires()))
            );
        }

        FeedEntity.Builder builder = FeedEntity.newBuilder()
                .setAlert(alert)
                .setId(statusType.getId());
        return builder.build();
    }

    private EntitySelector routeToInformedEntity(String id, RouteinfoType route) {
        Builder builder = EntitySelector.newBuilder();

        builder.setRouteId(route.getRoute());
        //Presently we can do this because there isn't any additional information that is needed on the TripDescriptor
        if(_includeDirecton) {
            builder.setTrip(routeDetailsForTripDescriptor(route));
        }

        return builder.build();
    }

    private GtfsRealtime.TripDescriptor routeDetailsForTripDescriptor(RouteinfoType route){
        GtfsRealtime.TripDescriptor.Builder builder = GtfsRealtime.TripDescriptor.newBuilder();

        if(route.getDirection().equals("S"))
        {
            builder.setDirectionId(0);
        } else if (route.getDirection().equals("N"))
        {
            builder.setDirectionId(1);
        }

        return GtfsRealtime.TripDescriptor.newBuilder().build();
    }

    private List<RouteinfoType> findRouteinfoTypeFromStatus(String statusTypeId, List<RouteinfoType> routes) {

        List<RouteinfoType> matchingRoutes = new ArrayList<RouteinfoType>();

        for (RouteinfoType route : routes) {
            if( route.getId().equals(statusTypeId) ) {
                matchingRoutes.add(route);
            }
        }

        return matchingRoutes;
    }

    private long StatusTypeBeginStringToDateTime(String dateString) {
        try{
            return ( (DateTime.parse(dateString, DateTimeFormat.forPattern("MM/dd/yyyy")).getMillis()) / 1000);
        }catch (Exception e)
        {
            _log.error("Failed to convert begin date "+e);
        }

        return DateTime.now().getMillis();
    }

    private long StatusTypeExpiresStringToDateTime(String dateString) {
        try{
            return ( (DateTime.parse(dateString, DateTimeFormat.forPattern("MM/dd/yyyy hh:mm a")).getMillis()) / 1000);
        }catch (Exception e)
        {
            _log.error("Failed to convert expires date "+e);
        }

        return DateTime.now().getMillis();
    }

    private static GtfsRealtime.TranslatedString.Builder translatedString(String text) {

        return GtfsRealtime.TranslatedString.newBuilder()
                .addTranslation(GtfsRealtime.TranslatedString.Translation.newBuilder()
                        .setText(text)
                        .setLanguage("en"));
    }
}


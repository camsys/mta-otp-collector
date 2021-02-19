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
package com.camsys.shims.gtfsrt.alerts.siri.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.Alert;
import com.google.transit.realtime.GtfsRealtime.EntitySelector;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TimeRange;
import com.google.transit.realtime.GtfsRealtime.TranslatedString;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert;
import com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.Builder;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.nyc.gtfsrt.util.GtfsRealtimeLibrary;
import org.onebusaway.nyc.transit_data_manager.model.ExtendedServiceAlertBean;
import org.onebusaway.nyc.transit_data_manager.util.NycSiriUtil;
import org.onebusaway.transit_data.model.service_alerts.NaturalLanguageStringBean;
import org.onebusaway.transit_data.model.service_alerts.ServiceAlertBean;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
import org.onebusaway.transit_data.model.service_alerts.SituationConsequenceBean;
import org.onebusaway.transit_data.model.service_alerts.TimeRangeBean;

import uk.org.siri.siri.Siri;

import java.util.Date;
import java.util.List;

public class SiriToGtfsrtTransformer implements GtfsRealtimeTransformer<Siri> {

    private GtfsRouteAdapter _gtfsRouteAdapter;

    /**
     * If this is greater than 0, set alerts without an end time to be the current time plus this many seconds.
     */
    private int _autoExpirySec = -1;

    @Override
    public FeedMessage transform(Siri siri) {
        List<ExtendedServiceAlertBean> serviceAlerts = NycSiriUtil.getSiriAsExtendedServiceAlertBeans(siri);

        FeedMessage.Builder message = FeedMessage.newBuilder();
        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        for (ExtendedServiceAlertBean serviceAlert : serviceAlerts) {
            if (_gtfsRouteAdapter != null)
                replaceRouteIds(serviceAlert);
            FeedEntity.Builder fe = FeedEntity.newBuilder();
            Alert.Builder alert = makeAlert(serviceAlert);

            if(serviceAlert.getReason() != null) {
            	GtfsRealtimeServiceStatus.MercuryAlert.Builder mercuryBuilder = 
            		GtfsRealtimeServiceStatus.MercuryAlert.newBuilder();

            	mercuryBuilder.setCreatedAt(serviceAlert.getCreationTime()/1000);
            	mercuryBuilder.setUpdatedAt(serviceAlert.getCreationTime()/1000);
            	mercuryBuilder.setDisplayBeforeActive(0);
            	mercuryBuilder.setAlertType(serviceAlert.getReason());
            	
            	alert.setExtension(GtfsRealtimeServiceStatus.mercuryAlert, mercuryBuilder.build());
            }
        
            if (_autoExpirySec > 0) {
                for (TimeRange.Builder timeRange : alert.getActivePeriodBuilderList()) {
                    if (!timeRange.hasEnd()) {
                        long end = (new Date().getTime()/1000) + _autoExpirySec;
                        timeRange.setEnd(end);
                    }
                }
            }
            fe.setAlert(alert);
            if (serviceAlert != null)
                fe.setId(serviceAlert.getId());
            message.addEntity(fe.build());
        }

        return message.build();
    }

    private void replaceRouteIds(ServiceAlertBean alert) {
        for (SituationAffectsBean affects : alert.getAllAffects()) {
            if (affects.getRouteId() != null) {
                String routeId = _gtfsRouteAdapter.getGtfsRouteId(affects);
                if (routeId != null)
                    affects.setRouteId(routeId);
            }
        }
    }

    public void setGtfsRouteAdapter(GtfsRouteAdapter gtfsRouteAdapter) {
        _gtfsRouteAdapter = gtfsRouteAdapter;
    }

    public void setAutoExpirySec(int autoExpirySec) {
        _autoExpirySec = autoExpirySec;
    }
    
    
    
    
    /***
     * This is all pulled from GtfsRealtimeLibrary in OBA NYC
     */
    
    private static Alert.Builder makeAlert(ExtendedServiceAlertBean alert) {

        Alert.Builder rtAlert = Alert.newBuilder();

        if (alert.getPublicationWindows() != null) {
            for (TimeRangeBean bean : alert.getPublicationWindows()) {
                rtAlert.addActivePeriod(range(bean));
            }
        }

        if (alert.getAllAffects() != null) {
            for (SituationAffectsBean affects : alert.getAllAffects()) {
                rtAlert.addInformedEntity(informedEntity(affects, alert));
            }
        }

        if (alert.getConsequences() != null && !alert.getConsequences().isEmpty()) {
            SituationConsequenceBean cb = alert.getConsequences().get(0);
            // Effect and EEffect perfectly match string values
            rtAlert.setEffect(Alert.Effect.valueOf(cb.getEffect().toString()));
        }

        if (alert.getUrls() != null) {
            rtAlert.setUrl(translatedString(alert.getUrls()));
        }

        if (alert.getSummaries() != null) {
            rtAlert.setHeaderText(translatedString(alert.getSummaries()));
        }

        if (alert.getDescriptions() != null) {
            rtAlert.setDescriptionText(translatedString(alert.getDescriptions()));
        }

        return rtAlert;
    }
    
    private static TimeRange.Builder range(TimeRangeBean range) {
        TimeRange.Builder builder = TimeRange.newBuilder();
        if (range.getFrom() > 0)
            builder.setStart(range.getFrom()/1000);
        if (range.getTo() > 0)
            builder.setEnd(range.getTo()/1000);
        return builder;
    }

    private static EntitySelector.Builder informedEntity(SituationAffectsBean bean, ExtendedServiceAlertBean alert) {
        EntitySelector.Builder builder = EntitySelector.newBuilder();

        // If there is a trip ID or a direction ID, use a TripDescriptor (no duplicate route info)
        if (bean.getTripId() != null || bean.getDirectionId() != null) {
            TripDescriptor.Builder td = TripDescriptor.newBuilder();
            if (bean.getTripId() != null)
                td.setTripId(id(bean.getTripId()));
            if (bean.getRouteId() != null)
                td.setRouteId(id(bean.getRouteId()));
            if (bean.getDirectionId() != null)
                td.setDirectionId(Integer.parseInt(bean.getDirectionId()));
            builder.setTrip(td);
        } else if (bean.getRouteId() != null) {
            builder.setRouteId(id(bean.getRouteId()));
        }

        if (bean.getRouteId() != null)
            builder.setAgencyId(AgencyAndId.convertFromString(bean.getRouteId()).getAgencyId());
        if (bean.getAgencyId() != null)
            builder.setAgencyId(bean.getAgencyId());
        if (bean.getStopId() != null)
            builder.setStopId(id(bean.getStopId()));

    	String routeId = builder.getRouteId();
    	if(builder.getTrip() != null) 
    		routeId = builder.getTrip().getRouteId();

    	if(builder.getAgencyId() != null && routeId != null && alert.getMessagePriority() != null) {
        	GtfsRealtimeServiceStatus.MercuryEntitySelector.Builder mercurySelectorBuilder = 
            		GtfsRealtimeServiceStatus.MercuryEntitySelector.newBuilder();

    		mercurySelectorBuilder.setSortOrder(builder.getAgencyId() + ":" + 
    			routeId + ":" + alert.getMessagePriority());
    	
    		builder.setExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector, mercurySelectorBuilder.build());
    	}
    	
        return builder;
    }
    
    private static TranslatedString.Builder translatedString(List<NaturalLanguageStringBean> beans) {
        TranslatedString.Builder string = TranslatedString.newBuilder();
        for (NaturalLanguageStringBean bean : beans) {
            TranslatedString.Translation.Builder tr = TranslatedString.Translation.newBuilder();
            tr.setLanguage(bean.getLang());
            tr.setText(bean.getValue());
            string.addTranslation(tr);
        }
        return string;
    }

    private static String id(String agencyAndId) {
        return AgencyAndId.convertFromString(agencyAndId).getId();
    }
}

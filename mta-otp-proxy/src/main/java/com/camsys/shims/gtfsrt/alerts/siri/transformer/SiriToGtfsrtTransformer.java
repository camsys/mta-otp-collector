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
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TimeRange;
import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert;
import com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.Builder;

import org.onebusaway.nyc.gtfsrt.util.GtfsRealtimeLibrary;
import org.onebusaway.nyc.transit_data_manager.util.NycSiriUtil;
import org.onebusaway.transit_data.model.service_alerts.ServiceAlertBean;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
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
        List<ServiceAlertBean> serviceAlerts = NycSiriUtil.getSiriAsServiceAlertBeans(siri);

        FeedMessage.Builder message = FeedMessage.newBuilder();
        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        for (ServiceAlertBean serviceAlert : serviceAlerts) {
            if (_gtfsRouteAdapter != null)
                replaceRouteIds(serviceAlert);
            FeedEntity.Builder fe = FeedEntity.newBuilder();
            Alert.Builder alert = GtfsRealtimeLibrary.makeAlert(serviceAlert);

            if(serviceAlert.getReason() != null) {
            	GtfsRealtimeServiceStatus.MercuryAlert.Builder mercuryBuilder = 
            		GtfsRealtimeServiceStatus.MercuryAlert.newBuilder();

            	mercuryBuilder.setCreatedAt(serviceAlert.getCreationTime());
            	mercuryBuilder.setUpdatedAt(serviceAlert.getCreationTime());
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
}

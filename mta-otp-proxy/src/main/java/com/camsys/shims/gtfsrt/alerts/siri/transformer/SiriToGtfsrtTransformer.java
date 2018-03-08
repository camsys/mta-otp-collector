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
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.Alert;
import com.google.transit.realtime.GtfsRealtime.EntitySelector;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeConstants;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.nyc.gtfsrt.util.GtfsRealtimeLibrary;
import org.onebusaway.nyc.transit_data_manager.util.NycSiriUtil;
import org.onebusaway.transit_data.model.service_alerts.ServiceAlertBean;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class SiriToGtfsrtTransformer implements GtfsRealtimeTransformer<Siri> {

    private GtfsRouteAdapter _gtfsRouteAdapter;

    @Override
    public FeedMessage transform(Siri siri) {

        if(!siri.getServiceDelivery().getSituationExchangeDelivery().iterator().next().getSituations().getPtSituationElement().iterator().next().isPlanned())
        {
            System.out.println("IS NOT PLANNED");
        }

        siri = removeAllPlannedSiriNotifications(siri);

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
            fe.setAlert(alert);
            fe.setId(serviceAlert.getId());
            message.addEntity(fe.build());
        }

        return message.build();
    }

    private Siri removeAllPlannedSiriNotifications(Siri siri)
    {
        Iterator<SituationExchangeDeliveryStructure> situationExchangeDeliveryIterator =
                siri.getServiceDelivery().getSituationExchangeDelivery().iterator();

        List<SituationExchangeDeliveryStructure> situationExchangeDeliveryStructureList = new ArrayList<SituationExchangeDeliveryStructure>();
        while (situationExchangeDeliveryIterator.hasNext())
        {
            SituationExchangeDeliveryStructure sed = situationExchangeDeliveryIterator.next();

            Iterator<PtSituationElementStructure> ptSituationElementStructureIterator = sed.getSituations().getPtSituationElement().iterator();

//            SituationExchangeDeliveryStructure.Situations situations = sed.getSituations();

            List<PtSituationElementStructure> finalPtElementList = new ArrayList<PtSituationElementStructure>();

            while (ptSituationElementStructureIterator.hasNext())
            {
                PtSituationElementStructure ptSituationElement = ptSituationElementStructureIterator.next();

                if(!ptSituationElement.isPlanned())
                {
                    finalPtElementList.add(ptSituationElement);
                }
            }

            SituationExchangeDeliveryStructure.Situations situations = new SituationExchangeDeliveryStructure.Situations();


            sed.setSituations();
            situationExchangeDeliveryStructureList.add(sed);
        }

        if(!siri.getServiceDelivery().getSituationExchangeDelivery().iterator().next().getSituations().getPtSituationElement().iterator().next().isPlanned())
        {
            System.out.println("IS NOT PLANNED");
        }


        return siri;
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
}

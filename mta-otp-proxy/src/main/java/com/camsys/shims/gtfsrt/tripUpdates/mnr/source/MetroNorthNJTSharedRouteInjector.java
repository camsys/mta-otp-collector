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
package com.camsys.shims.gtfsrt.tripUpdates.mnr.source;

import java.io.IOException;
import java.io.InputStream;

import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.source.TransformingGtfsRealtimeSource;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.kurtraschke.nyctrtproxy.FeedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MetroNorthNJTSharedRouteInjector extends TransformingGtfsRealtimeSource<GtfsRealtime.FeedMessage> {

	private static final Logger _log = LoggerFactory.getLogger(MetroNorthNJTSharedRouteInjector.class);

	private FeedManager njtFeedManager;

	public void setNjtFeedManager(FeedManager njtFeedManager) {
		this.njtFeedManager = njtFeedManager;
	}

	private String njtPortJervisRouteId;

	private String njtPascackValleyRouteId;

	private String njtUserAgent = "Mozilla/5.0 Firefox/26.0";
	
	@Override
	public GtfsRealtime.FeedMessage getMessage(String feedUrl, Deserializer<GtfsRealtime.FeedMessage> deserializer){
		try {
 			GtfsRealtime.FeedMessage.Builder responseBuilder = GtfsRealtime.FeedMessage.newBuilder();

			GtfsRealtime.FeedMessage mnrMessage = super.getMessage(feedUrl, deserializer);
			responseBuilder.mergeFrom(mnrMessage);

			GtfsRealtime.FeedMessage njtMessage = null;
			try {
				InputStream njtFeedStream = njtFeedManager.getStream("http://standards.xcmdata.org/TransitDE/rest/GTFSController/downloadProto", "NJT",njtUserAgent);
				njtMessage = deserializer.deserialize(njtFeedStream);
			} catch (Exception any) {
				_log.error("njt parsing failed with user agent " +njtUserAgent + ": ",  any);
				return mnrMessage;
			}

			if (njtMessage == null) {
				return mnrMessage;
			}

			for (int i = 0; i < njtMessage.getEntityCount(); i++) {
				FeedEntity entity = njtMessage.getEntity(i);
				if (entity.hasTripUpdate()) {
        			GtfsRealtime.TripUpdate.Builder tub = GtfsRealtime.TripUpdate.newBuilder();
            		tub.mergeFrom(entity.getTripUpdate());
            		String routeId = tub.getTripBuilder().getRouteId();

            		if(routeId==njtPascackValleyRouteId){
						tub.getTripBuilder().setRouteId("50");
					}
					if(routeId==njtPortJervisRouteId){
						tub.getTripBuilder().setRouteId("51");
            		}
            		
            		tub.clearStopTimeUpdate();

            		for(int ii = 0; ii < entity.getTripUpdate().getStopTimeUpdateCount(); ii++) {
            			StopTimeUpdate sourceStu = entity.getTripUpdate().getStopTimeUpdate(ii);		            			
            			StopTimeUpdate.Builder destStub = StopTimeUpdate.newBuilder();
            			destStub.mergeFrom(sourceStu);
            			
            			// remove these since they are relative to the feed's timestamp,
            			// and we'll be merging feeds
            			destStub.getDepartureBuilder().clearDelay();
            			destStub.getArrivalBuilder().clearDelay();
            			
            			tub.addStopTimeUpdate(destStub);
            		}
            		
            		FeedEntity.Builder feb = FeedEntity.newBuilder();
            		feb.setTripUpdate(tub.build());
            		feb.setId("COPIED-FROM-NJT-" + entity.getId());
            		
        			responseBuilder.addEntity(feb.build());
            	}
            }
			
 			GtfsRealtime.FeedHeader.Builder headerBuilder = GtfsRealtime.FeedHeader.newBuilder();
 			headerBuilder.mergeFrom(mnrMessage.getHeader());
			responseBuilder.setHeader(headerBuilder.build());

			return responseBuilder.build();

		} catch (Exception e) {
			_log.error("exception in NJR/MNR merge: ", e);
			return null;
		}
		
		
	}

	public void setNjtPascackValleyRouteId(String njtPascackValleyRouteId) {
		this.njtPascackValleyRouteId = njtPascackValleyRouteId;
	}

	public void setNjtPortJervisRouteId(String njtPortJervisRouteId) {
		this.njtPortJervisRouteId = njtPortJervisRouteId;
	}

	public void setNjtUserAgent(String njtUserAgent) {this.njtUserAgent = njtUserAgent;}
}

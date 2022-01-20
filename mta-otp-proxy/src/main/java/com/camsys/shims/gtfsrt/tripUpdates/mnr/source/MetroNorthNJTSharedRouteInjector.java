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

import org.apache.http.conn.HttpClientConnectionManager;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.camsys.shims.factory.CredentialFactory;
import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.source.TransformingGtfsRealtimeSource;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.kurtraschke.nyctrtproxy.FeedManager;

public class MetroNorthNJTSharedRouteInjector extends TransformingGtfsRealtimeSource<GtfsRealtime.FeedMessage> {

	private FeedManager njtFeedManager;

	public void setNjtFeedManager(FeedManager njtFeedManager) {
		this.njtFeedManager = njtFeedManager;
	}
	
	@Override
	public GtfsRealtime.FeedMessage getMessage(String feedUrl, Deserializer<GtfsRealtime.FeedMessage> deserializer){
		try {
			GtfsRealtime.FeedMessage.Builder responseBuilder = GtfsRealtime.FeedMessage.newBuilder();

			GtfsRealtime.FeedMessage mnrMessage = super.getMessage(feedUrl, deserializer);
			responseBuilder.mergeFrom(mnrMessage);
		
			InputStream njtFeedStream = njtFeedManager.getStream("http://standards.xcmdata.org/TransitDE/rest/GTFSController/downloadProto", "NJT");
			GtfsRealtime.FeedMessage njtMessage = deserializer.deserialize(njtFeedStream);
		
			 for (int i = 0; i < njtMessage.getEntityCount(); i++) {
		            FeedEntity entity = njtMessage.getEntity(i);
		            if (entity.hasTripUpdate()) {
		            	if(entity.getTripUpdate().getTrip().getRouteId().equals("6")) {
		        			GtfsRealtime.TripUpdate.Builder tub = GtfsRealtime.TripUpdate.newBuilder();
		            		tub.mergeFrom(entity.getTripUpdate());
		            		
		            		switch(tub.getTripBuilder().getRouteId()) {
		            			case "6":
		            				tub.getTripBuilder().setRouteId("51");
		            				break;

		            			case "13":
		            				tub.getTripBuilder().setRouteId("50");
		            				break;
		            			
		            			default:
		            				continue;
		            		}
		            		
		            		// clear stop IDs
		            		for(int ii = 0; i < tub.getStopTimeUpdateCount(); i++) {
		            			StopTimeUpdate.Builder stub = tub.getStopTimeUpdateBuilder(ii);		            			
		            			stub.clearStopId();
		            		}
		            		
		            		
		            		
		            		FeedEntity.Builder feb = FeedEntity.newBuilder();
		            		feb.setTripUpdate(tub.build());
		            		feb.setId("COPIED-FROM-NJT-" + entity.getId());
		            		
		        			responseBuilder.addEntity(feb.build());
		            	}
		            }
		     }
			
			
 			GtfsRealtime.FeedHeader.Builder headerBuilder = GtfsRealtime.FeedHeader.newBuilder();
 			headerBuilder.mergeFrom(mnrMessage.getHeader());
			responseBuilder.setHeader(headerBuilder.build());

			return responseBuilder.build();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
    
}

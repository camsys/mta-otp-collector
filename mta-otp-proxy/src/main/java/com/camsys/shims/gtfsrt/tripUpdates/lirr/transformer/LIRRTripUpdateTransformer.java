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
package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtimeLIRR;
import com.google.transit.realtime.GtfsRealtimeNYCT;

public class LIRRTripUpdateTransformer extends TripUpdateTransformer {

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe) {
        if (fe.hasTripUpdate()) {
            TripUpdate.Builder tripUpdate = fe.getTripUpdate().toBuilder();
            for (StopTimeUpdate.Builder stub : tripUpdate.getStopTimeUpdateBuilderList()) {
                String track = stub.getExtension(GtfsRealtimeLIRR.MtaStopTimeUpdate.track);
                if (track != null) {
                    GtfsRealtimeNYCT.NyctStopTimeUpdate.Builder nyctExt = GtfsRealtimeNYCT.NyctStopTimeUpdate.newBuilder();
                    nyctExt.setActualTrack(track);
                    stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
                }
            }
            return tripUpdate;
        }
        return null;
    }
}

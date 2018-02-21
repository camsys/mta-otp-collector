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
package com.camsys.shims.util.transformer;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;

public abstract class TripUpdateTransformer implements GtfsRealtimeTransformer<FeedMessage> {

    @Override
    public FeedMessage transform(FeedMessage message) {
        FeedMessage.Builder builder = FeedMessage.newBuilder();
        builder.setHeader(message.getHeader());
        for (int i = 0; i < message.getEntityCount(); i++) {
            FeedEntity entity = message.getEntity(i);
            if (entity.hasTripUpdate()) {
                TripUpdate.Builder tu = transformTripUpdate(entity);
                if (tu != null) {
                    FeedEntity.Builder feb = entity.toBuilder().setTripUpdate(tu);
                    builder.addEntity(feb);
                }
            } else {
                builder.addEntity(entity);
            }
        }
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(FeedEntity fe);
}

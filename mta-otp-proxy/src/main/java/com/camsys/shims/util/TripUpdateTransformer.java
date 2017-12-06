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
package com.camsys.shims.util;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;

public abstract class TripUpdateTransformer implements GtfsRealtimeTransformer {
    @Override
    public FeedMessage transform(FeedMessage message) {
        FeedMessage.Builder builder = message.toBuilder();
        for (int i = 0; i < builder.getEntityCount(); i++) {
            FeedEntity entity = builder.getEntity(i);
            if (entity.hasTripUpdate()) {
                FeedEntity.Builder fe = entity.toBuilder().setTripUpdate(
                        transformTripUpdate(entity.getTripUpdate()));
                builder.setEntity(i, fe);
            }
        }
        return builder.build();
    }

    public abstract TripUpdate.Builder transformTripUpdate(TripUpdate tu);
}

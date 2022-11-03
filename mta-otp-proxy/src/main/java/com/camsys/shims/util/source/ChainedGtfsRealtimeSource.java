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
package com.camsys.shims.util.source;

import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;
import org.apache.commons.lang.NotImplementedException;

/** This is a GtfsRealtimeSource that takes the output of a different source */
public class ChainedGtfsRealtimeSource implements GtfsRealtimeSource {

    private GtfsRealtimeSource _upstream;

    private GtfsRealtimeTransformer<FeedMessage> _transformer;

    public void setUpstream(GtfsRealtimeSource upstream) {
        _upstream = upstream;
    }

    public void setTransformer(GtfsRealtimeTransformer<FeedMessage> transformer) {
        _transformer = transformer;
    }

    @Override
    public FeedMessage getFeed() {
        FeedMessage message = _upstream.getFeed();
        message = _transformer.transform(message);
        return message;
    }

    @Override
    public void addIncrementalListener(GtfsRealtimeIncrementalListener gtfsRealtimeIncrementalListener) {
        throw new NotImplementedException();
    }

    @Override
    public void removeIncrementalListener(GtfsRealtimeIncrementalListener gtfsRealtimeIncrementalListener) {
        throw new NotImplementedException();
    }
}

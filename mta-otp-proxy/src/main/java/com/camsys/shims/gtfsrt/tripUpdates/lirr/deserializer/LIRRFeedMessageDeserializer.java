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
package com.camsys.shims.gtfsrt.tripUpdates.lirr.deserializer;

import com.camsys.shims.util.deserializer.Deserializer;
import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeLIRR;
import com.google.transit.realtime.GtfsRealtimeNYCT;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>LIRRFeedMessageDeserializer class.</p>
 *
 */
public class LIRRFeedMessageDeserializer implements Deserializer<GtfsRealtime.FeedMessage> {
    private static final ExtensionRegistry _extensionRegistry;

    static {
        _extensionRegistry = ExtensionRegistry.newInstance();
        GtfsRealtimeNYCT.registerAllExtensions(_extensionRegistry);
        GtfsRealtimeLIRR.registerAllExtensions(_extensionRegistry);
    }

    /** {@inheritDoc} */
    @Override
    public GtfsRealtime.FeedMessage deserialize(InputStream inputStream) throws IOException {
        GtfsRealtime.FeedMessage message = GtfsRealtime.FeedMessage.parseFrom(inputStream, _extensionRegistry);
        if (!message.getEntityList().isEmpty())
            return message;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getMimeType() {
        return "application/x-protobuf";
    }
}

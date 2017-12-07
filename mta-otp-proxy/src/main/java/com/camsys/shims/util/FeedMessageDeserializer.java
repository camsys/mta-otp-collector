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

import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeExtensions;

import java.io.IOException;
import java.io.InputStream;

public class FeedMessageDeserializer implements Deserializer<FeedMessage> {

    private static final ExtensionRegistry _extensionRegistry;

    static {
        _extensionRegistry = ExtensionRegistry.newInstance();
        GtfsRealtimeExtensions.registerExtensions(_extensionRegistry);
    }

    @Override
    public FeedMessage deserialize(InputStream inputStream) throws IOException {
        FeedMessage message = FeedMessage.parseFrom(inputStream, _extensionRegistry);
        if (!message.getEntityList().isEmpty())
            return message;
        return null;
    }

    @Override
    public String getMimeType() {
        return "application/x-protobuf";
    }
}

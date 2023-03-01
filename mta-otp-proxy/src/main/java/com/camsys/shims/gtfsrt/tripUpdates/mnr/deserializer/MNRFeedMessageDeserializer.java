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

package com.camsys.shims.gtfsrt.tripUpdates.mnr.deserializer;

import com.camsys.shims.util.deserializer.Deserializer;
import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeMTARR;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.googlecode.protobuf.format.JsonFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MNRFeedMessageDeserializer implements Deserializer<GtfsRealtime.FeedMessage> {
    private static final ExtensionRegistry _extensionRegistry;
	private Map<String, String> _headers;
	public void setApiHeaders(Map<String, String> headers) {
		_headers = headers;
	}
	@Override
	public Map<String, String> getApiHeaders() {
		return _headers;
	}

    private static final Logger _log = LoggerFactory.getLogger(MNRFeedMessageDeserializer.class);

    static {
        _extensionRegistry = ExtensionRegistry.newInstance();
        GtfsRealtimeNYCT.registerAllExtensions(_extensionRegistry);
        GtfsRealtimeMTARR.registerAllExtensions(_extensionRegistry);
    }

    @Override
    public GtfsRealtime.FeedMessage deserialize(InputStream inputStream) throws IOException {
    	if(getMimeType().equals("application/json")) {
    		StringBuilder textBuilder = new StringBuilder();
    	    try (Reader reader = new BufferedReader(new InputStreamReader
    	      (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
    	        int c = 0;
    	        while ((c = reader.read()) != -1) {
    	            textBuilder.append((char) c);
    	        }
    	    }

    	    // work around MTA's malformed JSON packet
    	    CharSequence newGuts = textBuilder;
    	    if(textBuilder.indexOf("{\"Result\":") != -1) {
	    	    int secondBracket = textBuilder.indexOf("{", 1);
	    	    int lastBracket = textBuilder.lastIndexOf("}");
	    	    int secondFromLastBracket = textBuilder.lastIndexOf("}", lastBracket - 1) + 1;
	    	    
	    	    newGuts = 
	    	    		textBuilder.subSequence(secondBracket, secondFromLastBracket);
    	    }
    	    
    		GtfsRealtime.FeedMessage.Builder builder = GtfsRealtime.FeedMessage.newBuilder();    		
    		JsonFormat jsonFormat = new JsonFormat();
    		jsonFormat.merge(newGuts, _extensionRegistry, builder);
    		
	    	GtfsRealtime.FeedMessage message = builder.build();
	        if (!message.getEntityList().isEmpty())
	            return message;    		
    	} else {
	    	GtfsRealtime.FeedMessage message = GtfsRealtime.FeedMessage.parseFrom(inputStream, _extensionRegistry);

	    	_log.info("Feed timestamp = " + new DateTime(message.getHeader().getTimestamp() * 1000));

	    	if (!message.getEntityList().isEmpty())
	            return message;
    	}
        return null;
    }

    @Override
    public String getMimeType() {
        return "application/x-protobuf";
    }
}

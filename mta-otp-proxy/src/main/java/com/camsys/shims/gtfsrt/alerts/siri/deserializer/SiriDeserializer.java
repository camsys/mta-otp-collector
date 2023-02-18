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
package com.camsys.shims.gtfsrt.alerts.siri.deserializer;

import com.amazonaws.util.IOUtils;
import com.camsys.shims.util.deserializer.Deserializer;
import org.onebusaway.nyc.siri.support.SiriXmlSerializer;
import uk.org.siri.siri.Siri;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SiriDeserializer implements Deserializer<Siri> {

    private SiriXmlSerializer _siriXmlSerializer = new SiriXmlSerializer();
    private Map<String, String> _headers;
    public void setApiHeaders(Map<String, String> headers) {
        _headers = headers;
    }
    @Override
    public Map<String, String> getApiHeaders() {
        return _headers;
    }

    @Override
    public Siri deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);
        return deserialize(xml);
    }

    protected Siri deserialize(String xml) throws IOException {
        try {
            return _siriXmlSerializer.fromXml(xml);
        } catch(JAXBException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public String getMimeType() {
        return "text/xml";
    }
}

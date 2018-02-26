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
import uk.org.siri.siri.Siri;

import java.io.IOException;
import java.io.InputStream;

// deserialize SIRI and modify LongDescription
public class SiriDeserializerWithModifications extends SiriDeserializer {
    @Override
    public Siri deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);
        xml = xml.replace("<Description", "<LegacyDescription")
            .replace("</Description>", "</LegacyDescription>")
            .replace("<LongDescription>", "<Description>")
            .replace("</LongDescription>", "</Description>")
            .replace("<MessagePriority>", "<Priority>")
            .replace("</MessagePriority>", "</Priority>");
        return deserialize(xml);
    }
}

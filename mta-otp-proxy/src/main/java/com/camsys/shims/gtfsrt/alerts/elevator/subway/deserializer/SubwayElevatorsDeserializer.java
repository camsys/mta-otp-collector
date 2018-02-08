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
package com.camsys.shims.gtfsrt.alerts.elevator.subway.deserializer;

import com.camsys.mta.elevators.NYCOutagesType;
import com.camsys.shims.util.Deserializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

public class SubwayElevatorsDeserializer implements Deserializer<NYCOutagesType> {

    private Unmarshaller _um;

    public SubwayElevatorsDeserializer() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(NYCOutagesType.class);
        _um = jc.createUnmarshaller();
    }

    @Override
    public NYCOutagesType deserialize(InputStream inputStream) throws IOException {
        try {
            JAXBElement<NYCOutagesType> nycResponse;
            nycResponse = _um.unmarshal(new StreamSource(inputStream),
                    NYCOutagesType.class);

            NYCOutagesType o = nycResponse.getValue();
            if (o.getResponsecode().equals("0")) {
                return o;
            } else {
                return null;
            }
        } catch(JAXBException je) {
            throw new IOException(je);
        }
    }

    @Override
    public String getMimeType() {
        return "text/xml";
    }
}

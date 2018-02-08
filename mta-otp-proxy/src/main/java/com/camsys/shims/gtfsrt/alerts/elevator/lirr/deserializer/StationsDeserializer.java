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
package com.camsys.shims.gtfsrt.alerts.elevator.lirr.deserializer;

import com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStations;
import com.camsys.shims.util.Deserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class StationsDeserializer implements Deserializer<LirrStations> {

    private ObjectMapper _mapper;

    public StationsDeserializer() {
        _mapper = new ObjectMapper();
    }

    @Override
    public LirrStations deserialize(InputStream inputStream) throws IOException {
        try {
            LirrStations results = _mapper.readValue(inputStream,LirrStations.class);
            return results;
        } catch(JsonMappingException jme){
            jme.printStackTrace();
            throw new IOException(jme);
        } catch(IOException jpe) {
            jpe.printStackTrace();
            throw new IOException(jpe);
        }
    }

    @Override
    public String getMimeType() {
        return "text/json";
    }
}

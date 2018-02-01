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
package com.camsys.shims.mnrelevators;

import com.camsys.mta.elevators.NYCOutagesType;
import com.camsys.shims.model.mnrelevators.Station;
import com.camsys.shims.model.mnrelevators.StationResults;
import com.camsys.shims.util.Deserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class StationResultsDeserializer implements Deserializer<StationResults> {

    private ObjectMapper _mapper;

    public StationResultsDeserializer() {
        _mapper = new ObjectMapper();
    }

    @Override
    public StationResults deserialize(InputStream inputStream) throws IOException {
        try {
            StationResults results = _mapper.readValue(inputStream,StationResults.class);
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

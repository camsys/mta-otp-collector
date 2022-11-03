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
package com.camsys.shims.atis.csv;

import org.onebusaway.csv_entities.schema.annotations.CsvField;
import org.onebusaway.csv_entities.schema.annotations.CsvFields;

@CsvFields(filename = "entry.csv")
public class AtisGtfsEntry {
    private String atisId;

    private String gtfsId;

    @CsvField(optional = true)
    private String gtfsShortName;

    @CsvField(optional = true)
    private String gtfsLongName;

    public String getAtisId() {
        return atisId;
    }

    public void setAtisId(String atisId) {
        this.atisId = atisId;
    }

    public String getGtfsId() {
        return gtfsId;
    }

    public void setGtfsId(String gtfsId) {
        this.gtfsId = gtfsId;
    }

    public String getGtfsShortName() {
        return gtfsShortName;
    }

    public void setGtfsShortName(String gtfsShortName) {
        this.gtfsShortName = gtfsShortName;
    }

    public String getGtfsLongName() {
        return gtfsLongName;
    }

    public void setGtfsLongName(String gtfsLongName) {
        this.gtfsLongName = gtfsLongName;
    }
}

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

/**
 * <p>AtisGtfsEntry class.</p>
 *
 */
@CsvFields(filename = "entry.csv")
public class AtisGtfsEntry {
    private String atisId;

    private String gtfsId;

    @CsvField(optional = true)
    private String gtfsShortName;

    @CsvField(optional = true)
    private String gtfsLongName;

    /**
     * <p>Getter for the field <code>atisId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAtisId() {
        return atisId;
    }

    /**
     * <p>Setter for the field <code>atisId</code>.</p>
     *
     * @param atisId a {@link java.lang.String} object.
     */
    public void setAtisId(String atisId) {
        this.atisId = atisId;
    }

    /**
     * <p>Getter for the field <code>gtfsId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsId() {
        return gtfsId;
    }

    /**
     * <p>Setter for the field <code>gtfsId</code>.</p>
     *
     * @param gtfsId a {@link java.lang.String} object.
     */
    public void setGtfsId(String gtfsId) {
        this.gtfsId = gtfsId;
    }

    /**
     * <p>Getter for the field <code>gtfsShortName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsShortName() {
        return gtfsShortName;
    }

    /**
     * <p>Setter for the field <code>gtfsShortName</code>.</p>
     *
     * @param gtfsShortName a {@link java.lang.String} object.
     */
    public void setGtfsShortName(String gtfsShortName) {
        this.gtfsShortName = gtfsShortName;
    }

    /**
     * <p>Getter for the field <code>gtfsLongName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsLongName() {
        return gtfsLongName;
    }

    /**
     * <p>Setter for the field <code>gtfsLongName</code>.</p>
     *
     * @param gtfsLongName a {@link java.lang.String} object.
     */
    public void setGtfsLongName(String gtfsLongName) {
        this.gtfsLongName = gtfsLongName;
    }
}

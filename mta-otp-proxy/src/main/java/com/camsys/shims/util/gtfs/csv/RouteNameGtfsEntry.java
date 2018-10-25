package com.camsys.shims.util.gtfs.csv;

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

import org.onebusaway.csv_entities.schema.annotations.CsvFields;

/**
 * <p>RouteNameGtfsEntry class.</p>
 *
 */
@CsvFields(filename = "entry.csv")
public class RouteNameGtfsEntry {
    private String routeName;

    private String gtfsId;

    /**
     * <p>Getter for the field <code>routeName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRouteName() {
        return routeName;
    }

    /**
     * <p>Setter for the field <code>routeName</code>.</p>
     *
     * @param routeName a {@link java.lang.String} object.
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
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
}

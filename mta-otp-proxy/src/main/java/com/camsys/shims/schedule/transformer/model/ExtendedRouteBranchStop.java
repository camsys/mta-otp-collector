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
package com.camsys.shims.schedule.transformer.model;

/**
 * <p>ExtendedRouteBranchStop class.</p>
 *
 */
public class ExtendedRouteBranchStop extends RouteBranchStop {
    /**
     * <p>Constructor for ExtendedRouteBranchStop.</p>
     *
     * @param rbs a {@link com.camsys.shims.schedule.transformer.model.RouteBranchStop} object.
     */
    public ExtendedRouteBranchStop(RouteBranchStop rbs) {
        super(rbs);
    }

    private double lat;

    private double lon;

    /**
     * <p>Getter for the field <code>lat</code>.</p>
     *
     * @return a double.
     */
    public double getLat() {
        return lat;
    }

    /**
     * <p>Setter for the field <code>lat</code>.</p>
     *
     * @param lat a double.
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * <p>Getter for the field <code>lon</code>.</p>
     *
     * @return a double.
     */
    public double getLon() {
        return lon;
    }

    /**
     * <p>Setter for the field <code>lon</code>.</p>
     *
     * @param lon a double.
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}

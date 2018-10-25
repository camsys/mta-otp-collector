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
package com.camsys.shims.gtfsrt.alerts.elevator.subway.stops_provider;

import java.util.Collection;

/**
 * <p>ElevatorToStopsProvider interface.</p>
 *
 */
public interface ElevatorToStopsProvider {
    /**
     * <p>getStopsForElevator.</p>
     *
     * @param elevatorId a {@link java.lang.String} object.
     * @return a {@link java.util.Collection} object.
     */
    Collection<String> getStopsForElevator(String elevatorId);
}

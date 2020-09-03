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
package com.camsys.shims.healthcheck;

import com.camsys.shims.service_status.source.ServiceStatusMonitor;

import java.util.Date;
import java.util.Map;

public class HealthcheckModel {

    private Date serviceStatusLastUpdated;

    private int stopsForRoute;

    private Map<String, Long> lastExecutionTimeMap;

    public Date getServiceStatusLastUpdated() {
        return serviceStatusLastUpdated;
    }

    public int getStopsForRoute() {
        return stopsForRoute;
    }

    public Map<String, Long> getLastExecutionTimeMap() { return lastExecutionTimeMap; }

    public HealthcheckModel(Date serviceStatusLastUpdated, int stopsForRoute, ServiceStatusMonitor monitor) {
        this.serviceStatusLastUpdated = serviceStatusLastUpdated;
        this.stopsForRoute = stopsForRoute;
        this.lastExecutionTimeMap = monitor.getMap();
    }
}

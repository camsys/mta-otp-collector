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
package com.camsys.shims.service_status.adapters;

import com.camsys.shims.atis.AtisGtfsMap;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

public class AtisIdRouteAdapter implements GtfsRouteAdapter {

    private AtisGtfsMap _atisGtfsMap;

    private String _agencyId;

    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        String routeId = affectsBean.getRouteId();
        AgencyAndId id = _atisGtfsMap.getId(routeId);
        if (id == null)
            throw new IllegalArgumentException("no");
        if (id.getAgencyId().equals(_agencyId)) {
            return _agencyId + AgencyAndId.ID_SEPARATOR + id.getId();
        }
        else {
            throw new IllegalArgumentException("No GTFS ID");
        }
    }

    public void setAtisGtfsMap(AtisGtfsMap atisGtfsMap) {
        _atisGtfsMap = atisGtfsMap;
    }

    public void setAgencyId(String agencyId) {
        _agencyId = agencyId;
    }
}

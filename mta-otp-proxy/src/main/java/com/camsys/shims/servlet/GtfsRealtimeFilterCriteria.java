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
package com.camsys.shims.servlet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Represent fields a feed can be filtered against.
 */
public class GtfsRealtimeFilterCriteria {

  public static final String UPDATES_SINCE = "updatesSince";
  public static final String ROUTE_ID = "routeId";
  public static final String AGENCY_ID = "agencyId";
  public static final String XML_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  private String agencyId;
  private String routeId;
  private Date updatesSince;

  // test if the criteria is populated
  public boolean isEmpty() {
    return agencyId == null 
            && routeId == null
            && updatesSince == null;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public void setUpdatesSinceStr(String updatesSince) {
    if (updatesSince == null) return;
    SimpleDateFormat sdf = new SimpleDateFormat(XML_DATE_PATTERN);
    try {
      this.updatesSince = sdf.parse(updatesSince);
    } catch (ParseException e) {
      this.updatesSince = null;
    }

  }

  public void populateFromRequestMap(Map<String, String[]> parameterMap) {
    if (parameterMap == null) return;
    if (parameterMap.containsKey(AGENCY_ID))
      setAgencyId(getFirstParam(parameterMap, AGENCY_ID));
    if (parameterMap.containsKey(ROUTE_ID))
      setRouteId(getFirstParam(parameterMap, ROUTE_ID));
    if (parameterMap.containsKey(UPDATES_SINCE))
      setUpdatesSinceStr(getFirstParam(parameterMap, UPDATES_SINCE));

  }

  private String getFirstParam(Map<String, String[]> parameterMap, String key) {
    String value = parameterMap.get(key)[0];
    // treat empty consistently
    if (value == null || value.length() == 0 || value.equals("")) return null;
    return value;
  }

  public boolean hasAgencyId() {
    return agencyId != null;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public boolean hasRouteId() {
    return routeId != null;
  }

  public String getRouteId() {
    return routeId;
  }

  public boolean hasUpdatesSince() {
    return updatesSince != null;
  }

  public Date getUpdatesSince() {
    return updatesSince;
  }
}

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

import com.google.protobuf.Message;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Filter (exclude elements of) a GtfsRealtime feed based on GtfsRealtimeCriteria.
 */
public class GtfsRealtimeFilter {

  private static final String ALLOW_ALERTS_WITHOUT_UPDATED_DATE = "com.camsys.shims.servlet.GtfsRealtimeFilter.allowAlertsWithoutDate";
  private HttpServletRequest _request;
  public GtfsRealtimeFilter(HttpServletRequest req) {
    _request = req;
  }

  /**
   * Entry point into GtfsRealtimeFilter -- perform the evaluation of applicability
   * @param source
   * @return
   */
  public Message applyFilter(GtfsRealtimeSource source) {
    GtfsRealtimeFilterCriteria criteria = createFilterCriteria(_request);
    if (criteria.isEmpty()) {
      return source.getFeed();
    }
    return applyFilter(source.getFeed(), criteria);
  }

  private Message applyFilter(GtfsRealtime.FeedMessage feed, GtfsRealtimeFilterCriteria criteria) {
    if (feed == null) return null;
    GtfsRealtime.FeedMessage.Builder filtered = feed.newBuilderForType();
    filtered.setHeader(feed.getHeader());

    for (GtfsRealtime.FeedEntity entity :feed.getEntityList()) {
      if (entity.hasAlert()) {
        if (alertMatches(entity.getAlert(), criteria)) {
          filtered.addEntity(entity);
        }
      } else {
        // we are expecting an alert feed, pass through anything else as is
        filtered.addEntity(entity);
      }
    }

    return filtered.build();
  }

  private boolean alertMatches(GtfsRealtime.Alert alert, GtfsRealtimeFilterCriteria criteria) {

    // here we look for reason to deny the match -- the default is to accept
    // thus an empty criteria would accept anything
    for (GtfsRealtime.EntitySelector entitySelector : alert.getInformedEntityList()) {
      if (agencyIdMatch(criteria, entitySelector)
              && routeIdMatch(criteria, entitySelector)
              && updatesSinceMatch(criteria, alert)
              && inRange(criteria, alert)) {
        return true;
      }

    }

    return false;
  }

  // test if alert falls withing the specified range criteria
  private boolean inRange(GtfsRealtimeFilterCriteria criteria, GtfsRealtime.Alert alert) {
    // no range specified, short circuit evaluation
    if (!criteria.hasStartDate() && !criteria.hasEndDate()) return true;
    Date startDate = new Date(0);
    if (criteria.hasStartDate())
      startDate = criteria.getStartDate();
    Date endDate = new Date(Long.MAX_VALUE);
    if (criteria.hasEndDate())
      endDate = criteria.getEndDate();

    return inRange(alert, startDate, endDate);

  }

  // test if alert falls between startDate and endDate
  private boolean inRange(GtfsRealtime.Alert alert, Date startDate, Date endDate) {
    if (alert == null || alert.getActivePeriodList() == null) return true;
    boolean foundMatchingRange = false;
    for (GtfsRealtime.TimeRange activePeriod : alert.getActivePeriodList()) {
      Date periodStart = new Date(0);
      if (activePeriod.hasStart())
        periodStart = new Date(activePeriod.getStart() * 1000);
      Date periodEnd = new Date(Long.MAX_VALUE);
      if (activePeriod.hasEnd())
        periodEnd = new Date(activePeriod.getEnd() * 1000);
      foundMatchingRange = inRange(startDate, endDate, periodStart, periodEnd);
      if (foundMatchingRange) return true;
      // else keep looking
    }
    return false;
  }

  // do the actual date intersection check
  private boolean inRange(Date start1, Date end1, Date start2, Date end2) {
    return Math.max(start1.getTime(), start2.getTime()) < Math.min(end1.getTime(), end2.getTime());
  }

  private boolean updatesSinceMatch(GtfsRealtimeFilterCriteria criteria, GtfsRealtime.Alert alert) {
    if (!criteria.hasUpdatesSince()) return true;

    if (criteria.hasUpdatesSince()) {
      // we need to look at mercury extension to see updated date
      if (alert.hasExtension(GtfsRealtimeServiceStatus.mercuryAlert)) {
        GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert =
                alert.getExtension(GtfsRealtimeServiceStatus.mercuryAlert);
        if (mercuryAlert.hasUpdatedAt()
                // updatedAt is seconds, updatesSince is milliSeconds
                && mercuryAlert.getUpdatedAt()*1000 >= criteria.getUpdatesSince().getTime())
          return true;
      } else {
        // if the alert doesn't have the mercury extension, we don't have enough information
        // fall back to default policy
        if (acceptAlertWithoutUpdatedDate()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean routeIdMatch(GtfsRealtimeFilterCriteria criteria, GtfsRealtime.EntitySelector entitySelector) {
    return !criteria.hasRouteId()
            || entitySelector.hasRouteId() && entitySelector.getRouteId().equals(criteria.getRouteId())
            || entitySelector.hasTrip() && entitySelector.getTrip().hasRouteId()
                && entitySelector.getTrip().getRouteId().equals(criteria.getRouteId());
  }

  private boolean agencyIdMatch(GtfsRealtimeFilterCriteria criteria, GtfsRealtime.EntitySelector entitySelector) {

    return !criteria.hasAgencyId()
            || entitySelector.hasAgencyId() && entitySelector.getAgencyId().equals(criteria.getAgencyId());
  }

  private boolean acceptAlertWithoutUpdatedDate() {
    return System.getProperty(ALLOW_ALERTS_WITHOUT_UPDATED_DATE) != null
            && "true".equalsIgnoreCase(System.getProperty(ALLOW_ALERTS_WITHOUT_UPDATED_DATE));
  }

  private GtfsRealtimeFilterCriteria createFilterCriteria(HttpServletRequest request) {
    GtfsRealtimeFilterCriteria criteria = new GtfsRealtimeFilterCriteria();
    criteria.populateFromRequestMap(request.getParameterMap());
    return criteria;
  }
}

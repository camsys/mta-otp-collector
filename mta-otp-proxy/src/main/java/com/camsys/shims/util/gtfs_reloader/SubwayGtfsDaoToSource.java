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
package com.camsys.shims.util.gtfs_reloader;

import com.camsys.shims.util.gtfs.GtfsDaoDependency;
import com.kurtraschke.nyctrtproxy.services.LazyTripMatcher;
import com.kurtraschke.nyctrtproxy.services.TripActivator;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.services.calendar.CalendarServiceDataFactory;

/**
 * Manually keep track of CalendarServiceData dependencies in the RT shim.
 *  This is a bad pattern and should be revisited.
 *
 */
public class SubwayGtfsDaoToSource extends GtfsDaoToSource {

    private TripActivator _tripActivator;

    private LazyTripMatcher _lazyTripMatcher;

    /**
     * <p>setTripActivator.</p>
     *
     * @param tripActivator a {@link com.kurtraschke.nyctrtproxy.services.TripActivator} object.
     */
    public void setTripActivator(TripActivator tripActivator) {
        _tripActivator = tripActivator;
    }

    /**
     * <p>setLazyTripMatcher.</p>
     *
     * @param lazyTripMatcher a {@link com.kurtraschke.nyctrtproxy.services.LazyTripMatcher} object.
     */
    public void setLazyTripMatcher(LazyTripMatcher lazyTripMatcher) {
        _lazyTripMatcher = lazyTripMatcher;
    }

    /**
     * <p>init.</p>
     */
    public void init() {
        GtfsDaoDependency subwayDependencies = (dao) -> {
            CalendarServiceDataFactory csdf = new CalendarServiceDataFactoryImpl(dao);
            CalendarServiceData csd = csdf.createData();
            _tripActivator.setCalendarServiceData(csd);
            _lazyTripMatcher.setCalendarServiceData(csd);
        };
        addGtfsDependency(subwayDependencies);
    }
}

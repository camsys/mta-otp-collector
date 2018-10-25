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
package com.camsys.shims.factory;

import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.gtfs.services.calendar.CalendarServiceDataFactory;
import org.springframework.beans.factory.FactoryBean;

public class SpringCalendarServiceDataFactory implements FactoryBean<CalendarServiceData> {

    private GtfsRelationalDao _dao;

    @Override
    public CalendarServiceData getObject() throws Exception {
        CalendarServiceDataFactory csdf = new CalendarServiceDataFactoryImpl(_dao);
        return csdf.createData();
    }

    @Override
    public Class<?> getObjectType() {
        return CalendarServiceData.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setDao(GtfsRelationalDao dao) {
        _dao = dao;
    }
}

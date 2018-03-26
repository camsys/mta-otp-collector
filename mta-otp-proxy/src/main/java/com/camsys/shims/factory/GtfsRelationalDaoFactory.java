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

import org.joda.time.DateTime;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

// spring-ified from com.kurtrachke.nyctrtproxy.services.GtfsRelationalDaoProvider
public class GtfsRelationalDaoFactory implements FactoryBean<GtfsRelationalDao> {

    private static final Logger _log = LoggerFactory.getLogger(GtfsRelationalDaoFactory.class);

    private String _gtfsPath;
    private GtfsRelationalDao _gtfsDao;

    public void setGtfsPath(String gtfsPath) {
        _gtfsPath = gtfsPath;
    }
    public String getGtfsPath() {
        return _gtfsPath;
    }

    public GtfsRelationalDao getObject() {
        _log.info("Loading GTFS from {}", _gtfsPath.toString());
        UpdateableGtfsRelationalDao dao = new UpdateableGtfsRelationalDao();
        dao.setGtfsPath(_gtfsPath);
        dao.load();

        return dao;
    }

    public Class<?> getObjectType() {
        return GtfsRelationalDao.class;
    }
}

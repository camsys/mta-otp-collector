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

import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * <p>GtfsRelationalDaoFactory class.</p>
 *
 */
public class GtfsRelationalDaoFactory implements FactoryBean<GtfsRelationalDao> {

    private static final Logger _log = LoggerFactory.getLogger(GtfsRelationalDaoFactory.class);

    private String _gtfsPath;
    private GtfsRelationalDao _gtfsDao;

    /**
     * <p>setGtfsPath.</p>
     *
     * @param gtfsPath a {@link java.lang.String} object.
     */
    public void setGtfsPath(String gtfsPath) {
        _gtfsPath = gtfsPath;
    }
    /**
     * <p>getGtfsPath.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsPath() {
        return _gtfsPath;
    }

    /**
     * <p>getObject.</p>
     *
     * @return a {@link org.onebusaway.gtfs.services.GtfsRelationalDao} object.
     */
    public GtfsRelationalDao getObject() {
        _log.debug("Loading GTFS from {}", _gtfsPath.toString());
        UpdateableGtfsRelationalDao dao = new UpdateableGtfsRelationalDao();
        dao.setGtfsPath(_gtfsPath);
        dao.load();

        return dao;
    }

    /**
     * <p>getObjectType.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getObjectType() {
        return GtfsRelationalDao.class;
    }
}

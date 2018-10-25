package com.camsys.shims.util.gtfs_provider;

import org.onebusaway.gtfs.services.GtfsRelationalDao;

/**
 * <p>GtfsDaoProvider interface.</p>
 *
*/
public interface GtfsDaoProvider {
    /**
     * <p>getDaoForAgency.</p>
     *
     * @param agency a {@link java.lang.String} object.
     * @return a {@link org.onebusaway.gtfs.services.GtfsRelationalDao} object.
     */
    GtfsRelationalDao getDaoForAgency(String agency);
}

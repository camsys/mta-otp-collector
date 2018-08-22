package com.camsys.shims.util.gtfs_provider;

import org.onebusaway.gtfs.services.GtfsRelationalDao;

public interface GtfsDaoProvider {
    GtfsRelationalDao getDaoForAgency(String agency);
}

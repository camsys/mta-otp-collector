package com.camsys.shims.util.source;

import com.camsys.shims.factory.GtfsRelationalDaoFactory;

public class GtfsDaoToSource {
    GtfsRelationalDaoFactory _gtfsDao;
    public GtfsRelationalDaoFactory getGtfsRelationalDao() {
        return _gtfsDao;
    }
    public void setGtfsRelationalDao(GtfsRelationalDaoFactory gtfsDaoSourceUrl) {
        _gtfsDao = gtfsDaoSourceUrl;
    }

    String _gtfsDaoSourceUrl;
    public String getGtfsDaoSourceUrl() {
        return _gtfsDaoSourceUrl;
    }
    public void setGtfsDaoSourceUrl(String gtfsDaoSourceUrl) {
        _gtfsDaoSourceUrl = gtfsDaoSourceUrl;
    }

}

package com.camsys.shims.util.gtfs_reloader;

import com.camsys.shims.factory.UpdateableGtfsRelationalDao;
import com.camsys.shims.util.gtfs.GtfsDaoDependency;

import java.util.ArrayList;
import java.util.List;

public class GtfsDaoToSource {

    private UpdateableGtfsRelationalDao _gtfsDao;
    private String _gtfsDaoSourceUrl;
    private boolean _usesS3 = false;
    private String _saveLocation = "~/GTFS/saves";
    private List<GtfsDaoDependency> _gtfsDependencyList = new ArrayList<>();

    public UpdateableGtfsRelationalDao getGtfsRelationalDao() {
        return _gtfsDao;
    }
    public void setGtfsRelationalDao(UpdateableGtfsRelationalDao gtfsDao) {
        _gtfsDao = gtfsDao;
        _saveLocation = gtfsDao.getGtfsPath();
    }

    public String getGtfsDaoSourceUrl() {
        return _gtfsDaoSourceUrl;
    }
    public void setGtfsDaoSourceUrl(String gtfsDaoSourceUrl) {
        _gtfsDaoSourceUrl = gtfsDaoSourceUrl;
    }

    public String getSaveLocation() {
        return _saveLocation;
    }

    public void setSaveLocation(String _saveLocation) {
        this._saveLocation = _saveLocation;
    }

    public boolean getUsesS3() {
        return _usesS3;
    }

    public void setUsesS3(boolean _usesS3) {
        this._usesS3 = _usesS3;
    }

    public List<GtfsDaoDependency> getDownstreamDependencies(){
      return _gtfsDependencyList;
    }

    public void setGtfsDependencyList(List<GtfsDaoDependency> gtfsDependencyList) {
        _gtfsDependencyList = gtfsDependencyList;
    }

    public void addGtfsDependency(GtfsDaoDependency dependency) {
        _gtfsDependencyList.add(dependency);
    }
}

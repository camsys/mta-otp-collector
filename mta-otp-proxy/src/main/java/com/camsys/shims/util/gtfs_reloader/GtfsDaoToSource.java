package com.camsys.shims.util.gtfs_reloader;

import com.camsys.shims.factory.UpdateableGtfsRelationalDao;
import com.camsys.shims.util.gtfs.GtfsDaoDependency;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>GtfsDaoToSource class.</p>
 *
 */
public class GtfsDaoToSource {

    private UpdateableGtfsRelationalDao _gtfsDao;
    private String _gtfsDaoSourceUrl;
    private boolean _usesS3 = false;
    private String _saveLocation = "~/GTFS/saves";
    private List<GtfsDaoDependency> _gtfsDependencyList = new ArrayList<>();

    /**
     * <p>getGtfsRelationalDao.</p>
     *
     * @return a {@link com.camsys.shims.factory.UpdateableGtfsRelationalDao} object.
     */
    public UpdateableGtfsRelationalDao getGtfsRelationalDao() {
        return _gtfsDao;
    }
    /**
     * <p>setGtfsRelationalDao.</p>
     *
     * @param gtfsDao a {@link com.camsys.shims.factory.UpdateableGtfsRelationalDao} object.
     */
    public void setGtfsRelationalDao(UpdateableGtfsRelationalDao gtfsDao) {
        _gtfsDao = gtfsDao;
        _saveLocation = gtfsDao.getGtfsPath();
    }

    /**
     * <p>getGtfsDaoSourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsDaoSourceUrl() {
        return _gtfsDaoSourceUrl;
    }
    /**
     * <p>setGtfsDaoSourceUrl.</p>
     *
     * @param gtfsDaoSourceUrl a {@link java.lang.String} object.
     */
    public void setGtfsDaoSourceUrl(String gtfsDaoSourceUrl) {
        _gtfsDaoSourceUrl = gtfsDaoSourceUrl;
    }

    /**
     * <p>getSaveLocation.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSaveLocation() {
        return _saveLocation;
    }

    /**
     * <p>setSaveLocation.</p>
     *
     * @param _saveLocation a {@link java.lang.String} object.
     */
    public void setSaveLocation(String _saveLocation) {
        this._saveLocation = _saveLocation;
    }

    /**
     * <p>getUsesS3.</p>
     *
     * @return a boolean.
     */
    public boolean getUsesS3() {
        return _usesS3;
    }

    /**
     * <p>setUsesS3.</p>
     *
     * @param _usesS3 a boolean.
     */
    public void setUsesS3(boolean _usesS3) {
        this._usesS3 = _usesS3;
    }

    /**
     * <p>getDownstreamDependencies.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<GtfsDaoDependency> getDownstreamDependencies(){
      return _gtfsDependencyList;
    }

    /**
     * <p>setGtfsDependencyList.</p>
     *
     * @param gtfsDependencyList a {@link java.util.List} object.
     */
    public void setGtfsDependencyList(List<GtfsDaoDependency> gtfsDependencyList) {
        _gtfsDependencyList = gtfsDependencyList;
    }

    /**
     * <p>addGtfsDependency.</p>
     *
     * @param dependency a {@link com.camsys.shims.util.gtfs.GtfsDaoDependency} object.
     */
    public void addGtfsDependency(GtfsDaoDependency dependency) {
        _gtfsDependencyList.add(dependency);
    }
}

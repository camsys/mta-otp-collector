package com.camsys.shims.util.source;

import com.camsys.shims.factory.GtfsRelationalDaoFactory;
import com.camsys.shims.factory.UpdateableGtfsRelationalDao;
import com.camsys.shims.util.gtfs.GtfsAndCalendar;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.ArrayList;
import java.util.List;

public class GtfsDaoToSource {

    private UpdateableGtfsRelationalDao _gtfsDao;
    private String _gtfsDaoSourceUrl;
    private boolean _usesS3 = false;
    private String _saveLocation = "~/GTFS/saves";
    private List<GtfsAndCalendar> _gtfsAndCalendarList = new ArrayList<GtfsAndCalendar>();

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

    public List<GtfsAndCalendar> getGtfsAndCalendarList(){
      return _gtfsAndCalendarList;
    }
    public void setGtfsAndCalendarList(List<GtfsAndCalendar> list){
        _gtfsAndCalendarList = list;
    }
}

package com.camsys.shims.util.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class GtfsReloader {

    private static Logger _log = LoggerFactory.getLogger(GtfsReloader.class);

    List<GtfsDaoToSource> _daos;

    public List<GtfsDaoToSource> getDaosToRefresh() {
        return _daos;
    }
    public void setDaosToRefresh(List<GtfsDaoToSource> daosToRefresh) {
        this._daos = daosToRefresh;
    }

    public void downloadAndUpdateGtfs(){
        for (GtfsDaoToSource dao: _daos) {

            InputStream sourceResult = getGtfsSourceData(dao.getGtfsDaoSourceUrl());
            if(sourceResult != null){
                saveGtfsToDaoSource(sourceResult, dao._gtfsDao.getGtfsPath());
            }

            dao.getGtfsRelationalDao().getObject();
        }
    }

    private InputStream getGtfsSourceData(String daoSourceUrl) {
        try{
            URL url = new URL(daoSourceUrl);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();

            uc.setRequestMethod("GET");
            return uc.getInputStream();
        }catch (Exception e)
        {
            _log.error("Failed to retrieve gtfs source from " + daoSourceUrl + " " + e);
        }

        return null;
    }
    private void saveGtfsToDaoSource(InputStream source, String target) {
        try {
            PrintWriter out = new PrintWriter(target);
            out.print(source);
            out.close();
        } catch (Exception e)
        {
            _log.error("Failed to save gtfs_dao to " + target + " " + e);
        }
    }

}

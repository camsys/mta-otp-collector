package com.camsys.shims.util.source;

import com.amazonaws.services.s3.AmazonS3;
import com.camsys.shims.util.S3Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class GtfsReloader {

    private static Logger _log = LoggerFactory.getLogger(GtfsReloader.class);

    private List<GtfsDaoToSource> _daos;
    private String _user;
    private String _pass;

    public List<GtfsDaoToSource> getDaosToRefresh() {
        return _daos;
    }
    public void setDaosToRefresh(List<GtfsDaoToSource> daosToRefresh) {
        this._daos = daosToRefresh;
    }

    public void setUser (String user) { _user = user; }
    public void setPass (String pass) { _pass = pass; }

    public void downloadAndUpdateGtfs(){
        for (GtfsDaoToSource dao: _daos) {

            InputStream sourceResult;

            if(dao.getUsesS3())
            {
                AmazonS3 s3 = S3Utils.getS3Client(_user, _pass);
                sourceResult = S3Utils.getViaS3(s3, dao.getGtfsDaoSourceUrl());
            }else{
                sourceResult = getGtfsSourceDataWithoutS3(dao.getGtfsDaoSourceUrl());
            }

            if(sourceResult != null){
                saveGtfsToDaoSource(sourceResult, dao.getSaveLocation());
            }

            dao.getGtfsRelationalDao().load();
        }
    }

    private InputStream getGtfsSourceDataWithoutS3(String daoSourceUrl) {
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

            File updatedGtfs = new File(target);

            java.nio.file.Files.copy(source, updatedGtfs.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e)
        {
            _log.error("Failed to save gtfs_dao to " + target + " " + e);
        }
    }

}

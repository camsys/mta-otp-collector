package com.camsys.shims.factory;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;

public class UpdateableGtfsRelationalDao extends GtfsRelationalDaoImpl {

    private String _gtfsPath;

    public void setGtfsPath(String path) { _gtfsPath = path; }
    public String getGtfsPath() { return _gtfsPath; }

    public void load(){
        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(this);
        try {
            reader.setInputLocation(new File(_gtfsPath));
            reader.run();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }
    }

}

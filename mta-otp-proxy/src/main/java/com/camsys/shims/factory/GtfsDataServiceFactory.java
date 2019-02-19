/**
 * Copyright (C) 2019 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camsys.shims.factory;

import com.camsys.shims.util.gtfs_provider.GtfsDataServiceProvider;
import org.onebusaway.gtfs.impl.GtfsDataServiceImpl;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.io.File;
import java.io.IOException;

public class GtfsDataServiceFactory implements FactoryBean<GtfsDataService> {
    private static final Logger _log = LoggerFactory.getLogger(GtfsDataServiceFactory.class);

    private GtfsDataServiceProvider _provider;

    public void setProvider(GtfsDataServiceProvider provider) {
        _provider = provider;
    }

    private String _gtfsPath;

    public void setGtfsPath(String gtfsPath) {
        _gtfsPath = gtfsPath;
    }

    @Override
    public GtfsDataService getObject() throws Exception {
        GtfsRelationalDaoImpl dao = new GtfsRelationalDaoImpl();
        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(dao);
        try {
            reader.setInputLocation(new File(_gtfsPath));
            reader.run();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }
        GtfsDataServiceImpl dataService = new GtfsDataServiceImpl();
        dataService.setGtfsDao(dao);
        _provider.addGtfsDataService(dataService, _gtfsPath);
        return dataService;
    }

    @Override
    public Class<?> getObjectType() {
        return GtfsDataService.class;
    }

}

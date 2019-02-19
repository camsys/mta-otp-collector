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
package com.camsys.shims.util.gtfs_provider;

import com.camsys.shims.factory.GtfsDataServiceFactory;
import org.onebusaway.gtfs.impl.GtfsDataServiceImpl;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GtfsDataServiceProviderImpl implements GtfsDataServiceProvider {

    private static final Logger _log = LoggerFactory.getLogger(GtfsDataServiceProviderImpl.class);

    private static final long LOAD_DELAY_SEC = 10;

    private Map<String, LoadableService> _services = new HashMap<>();

    private boolean updating = false;

    @Override
    public void addGtfsDataService(GtfsDataService service, String path) {
        String feedId = service.getFeedId();
        if (_services.containsKey(feedId)) {
            _log.error("Multiple GTFS feeds for feed ID {}", feedId);
        }
        _services.put(feedId, new LoadableService(service, path));
    }

    @Override
    public GtfsDataService getGtfsDataService(String feedId) {
        LoadableService loadableService = _services.get(feedId);
        return loadableService != null ? loadableService.service : null;
    }

    // Reload GTFS if it's been changed on disk. This is inspired by OpenTripPlanner's InputStreamGraphSource
    public void update() {
        if (updating) {
            return;
        }
        updating = true;
        for (LoadableService loadableService : _services.values()) {
            if (shouldReload(loadableService)) {
                long lastUpdated = loadableService.getLastModified();
                GtfsRelationalDao dao = GtfsDataServiceFactory.readDao(loadableService.path);
                if (dao != null) {
                    _log.info("Resetting dao for service {}", loadableService.getFeedId());
                    if (loadableService.service instanceof GtfsDataServiceImpl) {
                        GtfsDataServiceImpl service = (GtfsDataServiceImpl) loadableService.service;
                        service.setGtfsDao(dao);
                        loadableService.lastModifiedAtLoad = lastUpdated;
                    }
                }
            }
        }
        updating = false;
    }

    boolean shouldReload(LoadableService service) {
        long validEndTime = System.currentTimeMillis() - LOAD_DELAY_SEC * 1000;
        long lastModified = service.getLastModified();
        _log.info("shouldReload feed {} validEndTime={} lastModified={} serviceLastModified={}",
                service.getFeedId(), validEndTime, lastModified, service.lastModifiedAtLoad);
        if (lastModified != service.lastModifiedAtLoad && lastModified <= validEndTime) {
            _log.info("Feed {} input modification detected, force reload.", service.getFeedId());
            return true;
        } else {
            return false;
        }
    }

    private static class LoadableService {
        GtfsDataService service;
        String path;
        long lastModifiedAtLoad;

        LoadableService(GtfsDataService service, String path) {
            this.service = service;
            this.path = path;
            this.lastModifiedAtLoad = getLastModified();
        }

        long getLastModified() {
            // Note: this returns 0L if the file does not exists
            return new File(path).lastModified();
        }

        String getFeedId() {
            return service.getFeedId();
        }
    }
}

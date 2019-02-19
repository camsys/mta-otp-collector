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

import org.onebusaway.gtfs.services.GtfsDataService;

import java.util.HashMap;
import java.util.Map;

public class GtfsDataServiceProviderImpl implements GtfsDataServiceProvider {

    private Map<String, LoadableService> _services = new HashMap<>();

    @Override
    public void addGtfsDataService(GtfsDataService service, String path) {
        String feedId = service.getFeedId();
        _services.put(feedId, new LoadableService(service, path));
    }

    @Override
    public GtfsDataService getGtfsDataService(String feedId) {
        LoadableService loadableService = _services.get(feedId);
        return loadableService != null ? loadableService.service : null;
    }

    void update() {
        // todo
    }

    private static class LoadableService {
        GtfsDataService service;
        String path;

        LoadableService(GtfsDataService service, String path) {
            this.service = service;
            this.path = path;
        }
    }
}

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
package com.camsys.shims.util.otp;

import org.opentripplanner.routing.impl.GraphScanner;
import org.opentripplanner.routing.impl.InputStreamGraphSource;
import org.opentripplanner.routing.services.GraphService;
import org.springframework.beans.factory.FactoryBean;;

import java.io.File;
import java.util.Arrays;

public class OTPGraphServiceFactoryBean implements FactoryBean<GraphService> {

    private boolean _autoReload = true;

    private String _graphDirectory;

    @Override
    public GraphService getObject() throws Exception {
        // borrowed from OTPMain
        GraphService graphService = new GraphService(_autoReload);
        File graphDirectory = new File(_graphDirectory);
        InputStreamGraphSource.FileFactory graphSourceFactory =
                new InputStreamGraphSource.FileFactory(graphDirectory);
        graphService.graphSourceFactory = graphSourceFactory;
        if (_graphDirectory != null) {
            graphSourceFactory.basePath = graphDirectory;
        }

        // TODO: list of routers?
        GraphScanner graphScanner = new GraphScanner(graphService, graphDirectory, false);
        graphScanner.basePath = graphDirectory;
        graphScanner.defaultRouterId = "graph";
        graphScanner.autoRegister = Arrays.asList("graph");
        graphScanner.startup();

        return graphService;
    }

    @Override
    public Class<?> getObjectType() {
        return GraphService.class;
    }

    public void setAutoReload(boolean autoReload) {
        _autoReload = autoReload;
    }

    public void setGraphDirectory(String graphDirectory) {
        _graphDirectory = graphDirectory;
    }
}

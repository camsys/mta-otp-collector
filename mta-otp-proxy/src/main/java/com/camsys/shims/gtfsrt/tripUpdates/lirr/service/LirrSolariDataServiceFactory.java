package com.camsys.shims.gtfsrt.tripUpdates.lirr.service;

import org.onebusaway.gtfs.services.GtfsDataService;

import java.util.Set;

/**
 * Factory for easy construction LirrSolariDataService.
 */
public class LirrSolariDataServiceFactory {

    private LIRRSolariDataService service;
    private String endpointUrl = null;

    private String username = null;

    private String password = null;

    private String topic = null;

    private String namespace = null;

    private Set<String> stationIdWhitelist = null;

    private GtfsDataService gtfsDataService = null;


    public LIRRSolariDataService getService() {
        if (service == null) {
            service = new LIRRSolariDataService();
            service.setEndpointUrl(endpointUrl);
            service.setUsername(username);
            service.setPassword(password);
            service.setTopic(topic);
            service.setNamespace(namespace);
            service.setStationIdWhitelist(stationIdWhitelist);
            service.setGtfsDataService(gtfsDataService);
            service.connect();
        }
        return service;
    }

    public void setService(LIRRSolariDataService service) {
        this.service = service;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setStationIdWhitelist(Set<String> stationIdWhitelist) {
        this.stationIdWhitelist = stationIdWhitelist;
    }

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
        this.gtfsDataService = gtfsDataService;
    }
}

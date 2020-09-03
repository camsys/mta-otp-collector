package com.camsys.shims.service_status.source;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * keep track of last execution time of spring beans
 * to monitor task scheduler progress
 */
public class ServiceStatusMonitor implements Serializable {

    private Map<String, Long> _idToLastExecutionMap = new HashMap<>();

    public void ping(String id) {
        _idToLastExecutionMap.put(id, System.currentTimeMillis());
    }

    public String toString() {
        return _idToLastExecutionMap.toString();
    }

    public Map<String, Long> getMap() {
        return new HashMap<>(_idToLastExecutionMap);
    }
}

package com.camsys.shims.util.source;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Keep some metrics on feed timestamps.
 */
public class RealTimeMonitor implements Serializable {

  private final Map<String, Long> _idToLastUpdateMap = new HashMap<>();

  public void update(String id, long timestamp) {
    _idToLastUpdateMap.put(id, timestamp);
  }

  public void ping(String id) {
    _idToLastUpdateMap.put(id, System.currentTimeMillis());
  }

  public String toString() {
    return _idToLastUpdateMap.toString();
  }

  public Map<String, Long> getMap() {
    return new HashMap<>(_idToLastUpdateMap);
  }

}

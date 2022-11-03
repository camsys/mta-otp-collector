package com.camsys.shims.gtfsrt.tripUpdates.lirr.transformer;

import com.camsys.shims.s3.AbstractS3CsvProvider;
import com.csvreader.CsvReader;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// rewrite stop IDs from new IDs to old Ids. MTA IT is updating the IDs to a new set,
// but other systems which we integrate with have the old IDs, so this class allows
/// us to hide the change from downstream systems.
public class LirrPoiStopTransformStrategy extends AbstractS3CsvProvider implements StopIdTransformStrategy {

    private static final Logger _log = LoggerFactory.getLogger(LirrPoiStopTransformStrategy.class);

    // Station,New stop_id,Old stop_id,Code

    private static final String NEW_STOP_ID = "New stop_id";

    private static final String OLD_STOP_ID = "Old stop_id";

    private Map<String, String> _stopMap;

    @Override
    public String transform(String route, String direction, String stop) {
        if (!getStopMap().containsKey(stop)) {
            _log.error("No mapped stop for LIRR found: {}", stop);
            return stop;
        }
        return getStopMap().get(stop);
    }

    @Override
    public void processRecord(CsvReader reader) throws IOException {
        String oldId = reader.get(OLD_STOP_ID);
        String newId = reader.get(NEW_STOP_ID);
        // map new ID (from LIRR's perspective) to old ID
        getStopMap().put(newId, oldId);
    }

    private Map<String, String> getStopMap() {
        if (_stopMap == null) {
           _stopMap  = new HashMap<>();
        }
        return _stopMap;
    }
}

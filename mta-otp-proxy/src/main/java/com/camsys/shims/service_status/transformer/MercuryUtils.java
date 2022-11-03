package com.camsys.shims.service_status.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Provide some common utilities for working with the Mercury (Service Status)
 * GTFS-RT Extension.
 */
public class MercuryUtils {

    public static int DEFAULT_SORT_ORDER = 6;
    private static final Logger _log = LoggerFactory.getLogger(MercuryUtils.class);

    public BigInteger parseSortOrder(String sortOrder) {
        // from GtfsRealtimeServiceStatus: expect format of  "GTFS-ID:Priority"
        // Priority maps to GtfsRealtimeServiceStatus.Priority
        int pos = sortOrder.lastIndexOf(":");
        if (pos > 0)
            try {
                return BigInteger.valueOf(Integer.parseInt(sortOrder.substring(pos + 1)));
            } catch (NumberFormatException nfe) {
                _log.error("invalid sortOrder |" + sortOrder + "|");
                return BigInteger.valueOf(DEFAULT_SORT_ORDER);
            }
        _log.error("unexpected sortOrder |" + sortOrder + "|");
        return BigInteger.valueOf(DEFAULT_SORT_ORDER);

    }

}

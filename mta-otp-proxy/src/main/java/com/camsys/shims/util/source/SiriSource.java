package com.camsys.shims.util.source;

import uk.org.siri.siri.Siri;

/**
 * interface to provide common options on SIRI data sources.
 */
public interface SiriSource {
    void update();
    Siri getSiri();
}

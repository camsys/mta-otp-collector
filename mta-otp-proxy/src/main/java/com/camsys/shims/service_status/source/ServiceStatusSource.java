package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.ServiceStatus;

/**
 * <p>ServiceStatusSource interface.</p>
 *
 */
public interface ServiceStatusSource {
    /**
     * <p>update.</p>
     */
    void update();
    /**
     * <p>getStatus.</p>
     *
     * @param updatesSince a {@link java.lang.String} object.
     * @return a {@link com.camsys.shims.service_status.model.ServiceStatus} object.
     */
    ServiceStatus getStatus(String updatesSince);
}

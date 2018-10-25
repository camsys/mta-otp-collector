package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.ServiceStatus;

public interface ServiceStatusSource {
    void update();
    ServiceStatus getStatus(String updatesSince);
}

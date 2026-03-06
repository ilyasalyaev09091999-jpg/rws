package com.route.api.business.core.refdata.client;

import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import java.util.List;

/**
 * Контракт компонента RefDataClient в доменном слое route-api.
 */
public interface RefDataClient {
    List<PortDto> getPorts();

    List<LockDto> getLocks();
}

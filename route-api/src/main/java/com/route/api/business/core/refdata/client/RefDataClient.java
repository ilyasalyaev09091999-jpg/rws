package com.route.api.business.core.refdata.client;

import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import java.util.List;

public interface RefDataClient {
    List<PortDto> getPorts();

    List<LockDto> getLocks();
}

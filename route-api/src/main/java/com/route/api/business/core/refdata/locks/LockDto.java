package com.route.api.business.core.refdata.locks;

import java.util.Set;

public record LockDto(
        String name,
        Set<Long> nodeIds) {}

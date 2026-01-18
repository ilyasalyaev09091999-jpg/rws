package com.route.api.business.core.locks;

import java.util.Set;

public record LockDto(
        String id,
        String name,
        double latitude,
        double longitude,
        Set<Long> nodeIds) {}

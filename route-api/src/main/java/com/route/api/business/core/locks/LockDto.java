package com.route.api.business.core.locks;

public record LockDto(
        String id,
        String name,
        double latitude,
        double longitude,
        long nodeId) {}

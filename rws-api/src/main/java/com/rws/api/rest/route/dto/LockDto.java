package com.rws.api.rest.route.dto;

import java.util.Set;

public record LockDto(
        String id,
        String name,
        double latitude,
        double longitude,
        Set<Long> nodeIds) {}

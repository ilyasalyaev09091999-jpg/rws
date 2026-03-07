package com.route.api.business.core.refdata.locks;

import java.util.Set;

/**
 * DTO-запись LockDto для передачи данных внутри route-api и по gRPC.
 */
public record LockDto(
        String name,
        Set<Long> nodeIds) {}

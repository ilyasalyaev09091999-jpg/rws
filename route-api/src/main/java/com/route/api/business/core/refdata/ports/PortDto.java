package com.route.api.business.core.refdata.ports;

/**
 * DTO-запись PortDto для передачи данных внутри route-api и по gRPC.
 */
public record PortDto(
        double latitude,
        double longitude) {
}

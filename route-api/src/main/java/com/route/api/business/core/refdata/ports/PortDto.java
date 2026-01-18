package com.route.api.business.core.refdata.ports;

public record PortDto(
        String id,
        String name,
        double latitude,
        double longitude) {
}

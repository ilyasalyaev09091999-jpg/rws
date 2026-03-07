package com.route.api.business.core.enitites;

/**
 * DTO-запись NodeCoordinate для передачи данных внутри route-api и по gRPC.
 */
public record NodeCoordinate(long id, double lat, double lon) {}

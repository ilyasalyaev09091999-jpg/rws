package com.route.api.business.core.enitites;

import java.io.Serializable;

/**
 * DTO-запись RouteNode для передачи данных внутри route-api и по gRPC.
 */
public record RouteNode(int seq, long nodeId, double lat, double lon, double cost) implements Serializable {}

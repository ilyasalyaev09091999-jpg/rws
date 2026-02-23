package com.rws.api.rest.route.dto;

import java.io.Serializable;

public record RouteNode(int seq, long nodeId, double lat, double lon, double cost) implements Serializable {}

package com.route.api.business.manager;

import com.route.api.access_data.db.jdbc.repository.PgRoutingRepository;
import com.route.api.business.core.bbox.BBoxCreator;
import com.route.api.business.core.preparing.PrepareRouteResponse;
import com.route.api.rest.dto.RouteFinderRequest;
import com.route.api.business.core.enitites.RouteNode;
import com.route.api.rest.dto.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindRouteManager {

    private final PgRoutingRepository routingRepository;
    private final BBoxCreator bBoxCreator;
    private final PrepareRouteResponse prepareRouteResponse;

    public RouteFinderResponse findRoute(RouteFinderRequest request) {

        Long nodeIdA = routingRepository.findNearestNodeId(request.startLongitude(), request.startLatitude());
        Long nodeIdB = routingRepository.findNearestNodeId(request.endLongitude(), request.endLatitude());

        double[] bBox = bBoxCreator.findBBox(request.startLatitude(), request.startLongitude(), request.endLatitude(), request.endLongitude());

        List<RouteNode> route = routingRepository.findRoute(nodeIdA, nodeIdB, bBox);

        return prepareRouteResponse.prepare(route, request.departureTime(), request.speed());
    }
}

package com.route.api.business.manager;

import com.route.api.access_data.db.jdbc.repository.PgRoutingRepository;
import com.route.api.business.core.bbox.BBoxCreator;
import com.route.api.business.core.locks.LockClient;
import com.route.api.business.core.locks.LockDto;
import com.route.api.business.core.locks.LocksFilter;
import com.route.api.business.core.preparing.PrepareRouteResponse;
import com.route.api.rest.dto.RouteFinderRequest;
import com.route.api.business.core.enitites.RouteNode;
import com.route.api.rest.dto.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FindRouteManager {

    private final PgRoutingRepository routingRepository;
    private final BBoxCreator bBoxCreator;
    private final PrepareRouteResponse prepareRouteResponse;
    private final LockClient lockClient;
    private final LocksFilter locksFilter;

    public RouteFinderResponse findRoute(RouteFinderRequest request) {

        Long nodeIdA = routingRepository.findNearestNodeId(request.startLongitude(), request.startLatitude());
        Long nodeIdB = routingRepository.findNearestNodeId(request.endLongitude(), request.endLatitude());

        double[] bBox = bBoxCreator.findBBox(request.startLatitude(), request.startLongitude(), request.endLatitude(), request.endLongitude());

        List<RouteNode> route = routingRepository.findRoute(nodeIdA, nodeIdB, bBox);

        List<LockDto> allLocks = lockClient.getAllLocks();
        List<LockDto> routeLocks = locksFilter.filterLocksByRoute(allLocks, route);

        return prepareRouteResponse.prepare(route, request.departureTime(), request.speed(), routeLocks);
    }
}

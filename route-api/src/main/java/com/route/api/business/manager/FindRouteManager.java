package com.route.api.business.manager;

import com.route.api.access_data.db.jdbc.repository.GraphVersionRepository;
import com.route.api.access_data.db.jdbc.repository.PgRoutingRepository;
import com.route.api.business.core.bbox.BBoxCreator;
import com.route.api.business.core.exceptions.RouteNotFoundException;
import com.route.api.business.core.redis.RedisFindRoute;
import com.route.api.business.core.redis.RedisRouteType;
import com.route.api.business.core.refdata.RefDataClient;
import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.locks.LocksFilter;
import com.route.api.business.core.preparing.PrepareRouteResponse;
import com.route.api.business.core.refdata.ports.PortDto;
import com.route.api.business.core.timeroute.TimeRouteCalculator;
import com.route.api.rest.dto.RouteFinderRequest;
import com.route.api.business.core.enitites.RouteNode;
import com.route.api.rest.dto.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindRouteManager {

    private final GraphVersionRepository graphVersionRepository;
    private final PgRoutingRepository routingRepository;
    private final BBoxCreator bBoxCreator;
    private final PrepareRouteResponse prepareRouteResponse;
    private final RefDataClient refDataClient;
    private final LocksFilter locksFilter;
    private final TimeRouteCalculator routeCalculator;
    private final RedisFindRoute redisFindRoute;

    public RouteFinderResponse findRoute(RouteFinderRequest request) throws RouteNotFoundException {
        log.info("Find route request");

        Long graphVersion = graphVersionRepository.getGraphVersion();

        Long nodeIdA = routingRepository.findNearestNodeId(request.startLongitude(), request.startLatitude());
        Long nodeIdB = routingRepository.findNearestNodeId(request.endLongitude(), request.endLatitude());
        if (nodeIdA == null || nodeIdB == null) {
            log.warn("Node id not found. Node id A: {}, Node id B: {}", nodeIdA, nodeIdB);
            throw new RouteNotFoundException("Не удалось поостроить маршрут");
        }
        log.info("Node id A: {}, Node id B: {}", nodeIdA, nodeIdB);

        List<PortDto> allPorts = refDataClient.getAllPorts();
        log.info("Ports: {}", allPorts);
        RedisRouteType redisRouteType = RedisRouteType.defineRouteType(allPorts, request.startLongitude(), request.startLatitude(),
                request.endLongitude(), request.endLatitude());
        log.info("Route type: {}", redisRouteType);

        List<RouteNode> route;
        if (redisRouteType == RedisRouteType.PORT_TO_PORT) {
            route = redisFindRoute.findPortToPortRoute(nodeIdA, nodeIdB, graphVersion);
        } else {
            double[] bBox = bBoxCreator.findBBox(request.startLatitude(), request.startLongitude(), request.endLatitude(), request.endLongitude());
            route = redisFindRoute.findAdHocRoute(nodeIdA, nodeIdB, bBox, graphVersion);
        }

        if (route == null || route.isEmpty()) {
            log.warn("Route not found. start=({}, {}), end=({}, {}), departureTime={}",
                    request.startLatitude(), request.startLongitude(), request.endLatitude(), request.startLongitude(), request.departureTime());
            throw new RouteNotFoundException("Не удалось поостроить маршрут");
        }

        List<LockDto> allLocks = refDataClient.getAllLocks();
        log.info("All locks: {}", allLocks);
        List<LockDto> routeLocks = locksFilter.filterLocksByRoute(allLocks, route);
        log.info("Route locks: {}", routeLocks);

        long timeRoute = routeCalculator.calculate(route, request.speed(), routeLocks.size());
        log.info("Time of route: {}", timeRoute);

        return prepareRouteResponse.prepare(route, request.departureTime(), routeLocks, timeRoute);
    }
}

package com.route.api.business.manager;

import com.route.api.access_data.db.jdbc.repository.GraphVersionRepository;
import com.route.api.access_data.db.jdbc.repository.PgRoutingRepository;
import com.route.api.business.core.bbox.BBoxCreator;
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
import org.springframework.stereotype.Service;
import java.util.List;

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

    public RouteFinderResponse findRoute(RouteFinderRequest request) {

        Long graphVersion = graphVersionRepository.getGraphVersion();

        Long nodeIdA = routingRepository.findNearestNodeId(request.startLongitude(), request.startLatitude());
        Long nodeIdB = routingRepository.findNearestNodeId(request.endLongitude(), request.endLatitude());

        List<PortDto> allPorts = refDataClient.getAllPorts();
        RedisRouteType redisRouteType = RedisRouteType.defineRouteType(allPorts, request.startLongitude(), request.startLatitude(),
                request.endLongitude(), request.endLatitude());

        double[] bBox = bBoxCreator.findBBox(request.startLatitude(), request.startLongitude(), request.endLatitude(), request.endLongitude());

        List<RouteNode> route;
        if (redisRouteType == RedisRouteType.PORT_TO_PORT) {
            route = redisFindRoute.findPortToPortRoute(nodeIdA, nodeIdB, graphVersion);
        } else {
            route = redisFindRoute.findAdHocRoute(nodeIdA, nodeIdB, bBox, graphVersion);
        }

        List<LockDto> allLocks = refDataClient.getAllLocks();
        List<LockDto> routeLocks = locksFilter.filterLocksByRoute(allLocks, route);

        long timeRoute = routeCalculator.calculate(route, request.speed(), routeLocks.size());

        return prepareRouteResponse.prepare(route, request.departureTime(), routeLocks, timeRoute);
    }
}

package com.route.api.business.core.redis;

import com.route.api.access_data.db.jdbc.repository.PgRoutingRepository;
import com.route.api.business.core.enitites.RouteNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Компонент кэширования маршрутов в Redis: RedisFindRoute.
 */
@Service
@RequiredArgsConstructor
public class RedisFindRoute {

    private final PgRoutingRepository repository;

    /**
     * Выполняет операцию findPortToPortRoute в рамках бизнес-логики route-api.
     */
    @Cacheable(
            value = "route-port-port",
            key = "#sourceNode + '-' + #targetNode + '-' + #graphVersion"
    )
    public List<RouteNode> findPortToPortRoute(Long sourceNode, Long targetNode, long graphVersion) {
        return repository.findRoute(sourceNode, targetNode);
    }

    /**
     * Выполняет операцию findAdHocRoute в рамках бизнес-логики route-api.
     */
    @Cacheable(
            value = "route-ad-hoc",
            key = "#graphVersion + ':' + #sourceNode + '-' + #targetNode + '-' + (#bBox != null ? T(java.util.Arrays).hashCode(#bBox) : 'no-bbox')"
    )
    public List<RouteNode> findAdHocRoute(Long sourceNode, Long targetNode, double[] bBox, long graphVersion) {
        return repository.findRoute(sourceNode, targetNode, bBox);
    }
}

package com.route.api.business.core.refdata.locks;

import com.route.api.business.core.enitites.RouteNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Бизнес-компонент route-api: LocksFilter.
 */
@Service
public class LocksFilter {

    /**
     * Оставляет только шлюзы, которые присутствуют на маршруте.
     */
    public List<LockDto> filterLocksByRoute(List<LockDto> locks, List<RouteNode> route) {
        if (locks == null || locks.isEmpty()) {
            return List.of();
        }

        Set<Long> routeNodeIds = route.stream()
                .map(RouteNode::nodeId)
                .collect(Collectors.toSet());

        return locks.stream()
                .filter(lock ->
                        lock.nodeIds().stream()
                                .anyMatch(routeNodeIds::contains)
                )
                .toList();
    }
}

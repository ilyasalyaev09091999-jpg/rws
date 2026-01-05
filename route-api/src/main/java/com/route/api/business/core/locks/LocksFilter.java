package com.route.api.business.core.locks;

import com.route.api.business.core.enitites.RouteNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocksFilter {

    public List<LockDto> filterLocksByRoute(List<LockDto> locks, List<RouteNode> route) {
        Set<Long> routeNodeIds = route.stream()
                .map(RouteNode::nodeId)
                .collect(Collectors.toSet());

        return locks.stream()
                .filter(lock -> routeNodeIds.contains(lock.nodeId()))
                .toList();
    }
}

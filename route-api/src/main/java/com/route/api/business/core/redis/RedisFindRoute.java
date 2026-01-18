package com.route.api.business.core.redis.routetype;

import com.route.api.business.core.enitites.RouteNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisFindRoute {

    

    @Cacheable(
            value = "route-port-port",
            key = "#sourceNode + '-' + #targetNode + '-' + #graphVersion"
    )
    public List<RouteNode> findPortToPortRoute(Long sourceNode, Long targetNode, long graphVersion) {
        return repository.findRoute(sourceNode, targetNode);
    }

    @Cacheable(
            value = "route-ad-hoc",
            key = "#graphVersion + ':' + #sourceNode + '-' + #targetNode + '-' + (#bBox != null ? T(java.util.Arrays).hashCode(#bBox) : 'no-bbox')"
    )
    public List<RouteNode> findAdHocRoute(Long sourceNode, Long targetNode, double[] bBox, long graphVersion) {
        return repository.findRoute(sourceNode, targetNode, bBox);
    }
}

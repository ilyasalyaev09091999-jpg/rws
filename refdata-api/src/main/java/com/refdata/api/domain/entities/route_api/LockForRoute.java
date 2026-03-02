package com.refdata.api.domain.entities.route_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class LockForRoute {

    @ToString.Include
    private String name;
    private Set<Long> nodeIds = new HashSet<>();
}

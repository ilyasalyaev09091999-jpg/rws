package com.refdata.api.domain.entities.route_api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO шлюза для {@code route-api}.
 * <p>
 * Содержит название шлюза и набор идентификаторов узлов графа,
 * связанных с этим шлюзом.
 * </p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class LockForRoute {

    /**
     * Название шлюза.
     */
    @ToString.Include
    private String name;

    /**
     * Идентификаторы узлов графа, привязанных к шлюзу.
     */
    private Set<Long> nodeIds = new HashSet<>();
}

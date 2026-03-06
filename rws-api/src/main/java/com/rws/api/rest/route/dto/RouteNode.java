package com.rws.api.rest.route.dto;

import java.io.Serializable;

/**
 * Узел маршрута в REST-ответе.
 *
 * @param seq порядковый номер узла в маршруте.
 * @param nodeId идентификатор вершины графа водных путей.
 * @param lat широта точки узла.
 * @param lon долгота точки узла.
 * @param cost накопленная стоимость/вес на момент достижения узла.
 */
public record RouteNode(int seq, long nodeId, double lat, double lon, double cost) implements Serializable {
}

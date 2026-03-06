package com.rws.api.rest.route.dto;

/**
 * Минимальное REST-представление шлюза на маршруте.
 *
 * @param name название шлюза.
 */
public record LockDto(
        String name) {
}

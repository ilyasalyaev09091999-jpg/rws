package com.rws.api.rest.route.controller.error;

/**
 * Исключение предметной области {@code rws-api}, обозначающее,
 * что маршрут между заданными точками не найден.
 */
public class RouteNotFoundException extends Exception {

    /**
     * Создаёт исключение с человекочитаемым сообщением.
     *
     * @param message описание причины ошибки поиска маршрута.
     */
    public RouteNotFoundException(String message) {
        super(message);
    }
}

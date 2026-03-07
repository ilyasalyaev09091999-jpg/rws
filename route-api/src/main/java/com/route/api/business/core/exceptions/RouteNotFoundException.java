package com.route.api.business.core.exceptions;

/**
 * Исключение, сигнализирующее, что маршрут между заданными точками не найден.
 */
public class RouteNotFoundException extends Exception {

    /**
     * Создает исключение с описанием причины ошибки.
     *
     * @param message текст ошибки поиска маршрута.
     */
    public RouteNotFoundException(String message) {
        super(message);
    }
}

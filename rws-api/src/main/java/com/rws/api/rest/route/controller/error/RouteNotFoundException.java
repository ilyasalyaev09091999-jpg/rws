package com.route.api.business.core.exceptions;

public class RouteNotFoundException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public RouteNotFoundException(String message) {
        super(message);
    }

}

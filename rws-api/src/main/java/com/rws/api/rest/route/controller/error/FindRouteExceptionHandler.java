package com.rws.api.rest.route.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик ошибок REST-маршрутизации в {@code rws-api}.
 * <p>
 * Преобразует доменные исключения контроллера маршрутов в унифицированный
 * JSON-ответ с кодом и временной меткой.
 * </p>
 */
@ControllerAdvice
public class FindRouteExceptionHandler {

    /**
     * Обрабатывает ситуацию, когда маршрут не найден.
     *
     * @param ex исключение, описывающее причину отсутствия маршрута.
     * @return HTTP 400 с телом {@link FindRouteErrorResponse}.
     */
    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<FindRouteErrorResponse> handleRouteError(RouteNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new FindRouteErrorResponse("Route not found", ex.getMessage(), 400, LocalDateTime.now()));
    }
}

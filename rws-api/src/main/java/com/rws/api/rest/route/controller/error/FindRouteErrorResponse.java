package com.rws.api.rest.route.controller.error;

import java.time.LocalDateTime;

/**
 * Стандартизованное тело ошибки для REST-эндпоинта поиска маршрута.
 *
 * @param error краткий код/тип ошибки.
 * @param message детальное описание ошибки.
 * @param status HTTP-статус.
 * @param timestamp момент формирования ответа об ошибке.
 */
public record FindRouteErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp) {
}

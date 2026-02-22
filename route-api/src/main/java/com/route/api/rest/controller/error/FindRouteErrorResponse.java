package com.route.api.rest.controller.error;

import java.time.LocalDateTime;

public record FindRouteErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp) {
}

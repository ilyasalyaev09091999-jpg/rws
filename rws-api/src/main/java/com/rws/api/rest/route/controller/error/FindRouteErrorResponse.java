package com.rws.api.rest.route.controller.error;

import java.time.LocalDateTime;

public record FindRouteErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp) {
}

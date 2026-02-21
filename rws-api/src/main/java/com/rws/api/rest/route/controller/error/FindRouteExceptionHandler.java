package com.route.api.rest.controller.error;

import com.route.api.business.core.exceptions.RouteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class FindRouteExceptionHandler {

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<FindRouteErrorResponse> handleRouteError(RouteNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new FindRouteErrorResponse("Route not found", ex.getMessage(), 400, LocalDateTime.now()));
    }

}

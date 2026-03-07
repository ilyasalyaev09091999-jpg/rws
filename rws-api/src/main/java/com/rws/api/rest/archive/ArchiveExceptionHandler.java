package com.rws.api.rest.archive;

import com.rws.api.rest.archive.client.ArchiveApiUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.rws.api.rest.archive")
public class ArchiveExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ArchiveApiUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleArchiveApiUnavailable(ArchiveApiUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", "Archive API unavailable", "detail", ex.getMessage()));
    }
}

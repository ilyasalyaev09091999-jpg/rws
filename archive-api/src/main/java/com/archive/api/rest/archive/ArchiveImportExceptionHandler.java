package com.archive.api.rest.archive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * REST-обработчик ошибок импорта архива.
 */
@RestControllerAdvice(basePackages = "com.archive.api.rest.archive")
public class ArchiveImportExceptionHandler {

    /**
     * Преобразует {@link IllegalArgumentException} в {@code 400 Bad Request}.
     *
     * @param ex исключение валидации входных данных
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}
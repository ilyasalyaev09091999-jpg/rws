package com.rws.api.rest.archive;

import com.rws.api.rest.archive.client.ArchiveApiUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * REST-обработчик исключений для архивных эндпоинтов {@code rws-api}.
 *
 * <p>Приводит типовые исключения к единообразным ответам для UI/клиентов.</p>
 */
@RestControllerAdvice(basePackages = "com.rws.api.rest.archive")
public class ArchiveExceptionHandler {

    /**
     * Преобразует ошибки валидации и входных данных в {@code 400 Bad Request}.
     *
     * @param ex исключение, связанное с ошибочными данными запроса
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    /**
     * Преобразует транспортные ошибки {@code archive-api} в {@code 502 Bad Gateway}.
     *
     * @param ex ошибка связи с {@code archive-api}
     * @return ответ с общим сообщением и деталями причины
     */
    @ExceptionHandler(ArchiveApiUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleArchiveApiUnavailable(ArchiveApiUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", "Archive API unavailable", "detail", ex.getMessage()));
    }
}
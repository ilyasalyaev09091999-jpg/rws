package com.rws.api.rest.archive.client;

/**
 * Исключение, выбрасываемое при недоступности {@code archive-api} или транспортных ошибках
 * (таймауты, сетевые сбои и т.п.).
 */
public class ArchiveApiUnavailableException extends RuntimeException {

    /**
     * Создает исключение с сообщением и причиной.
     *
     * @param message человекочитаемое описание ошибки
     * @param cause исходное исключение транспортного уровня
     */
    public ArchiveApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
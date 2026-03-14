package com.archive.api.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Утилиты для парсинга дат.
 */
public final class DateParserUtils {

    private DateParserUtils() {
    }

    /**
     * Парсит дату в формате ISO-8601 ({@code yyyy-MM-dd}) или возвращает {@code null} для пустых значений.
     *
     * @param value строковая дата
     * @return {@link LocalDate} либо {@code null}, если строка пустая
     * @throws IllegalArgumentException если строка не соответствует ожидаемому формату
     */
    public static LocalDate parseIsoDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date: " + value);
        }
    }
}
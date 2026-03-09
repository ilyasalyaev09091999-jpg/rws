package com.archive.api.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class DateParserUtils {

    private DateParserUtils() {
    }

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

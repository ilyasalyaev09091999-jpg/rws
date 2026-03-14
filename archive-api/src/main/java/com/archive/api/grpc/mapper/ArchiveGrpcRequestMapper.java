package com.archive.api.grpc.mapper;

import com.archive.api.util.DateParserUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Маппер входных gRPC параметров в доменные типы.
 */
@Component
public class ArchiveGrpcRequestMapper {

    /**
     * Нормализует имя файла для импорта.
     *
     * @param fileName исходное имя файла
     * @return исходное имя или {@code "unknown.xlsx"} при пустом значении
     */
    public String normalizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "unknown.xlsx";
        }
        return fileName;
    }

    /**
     * Возвращает {@code null} для пустых строковых значений.
     *
     * @param value строковое значение
     * @return {@code null}, если строка пустая/пробельная, иначе исходное значение
     */
    public String nullIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    /**
     * Парсит дату из строки или возвращает {@code null}.
     *
     * @param value строковая дата
     * @return {@link LocalDate} либо {@code null}
     */
    public LocalDate parseDate(String value) {
        return DateParserUtils.parseIsoDateOrNull(value);
    }

    /**
     * Возвращает месяц или {@code null}, если значение не задано.
     *
     * @param month номер месяца (1..12) или 0
     * @return номер месяца или {@code null}
     */
    public Integer monthOrNull(int month) {
        return month > 0 ? month : null;
    }
}
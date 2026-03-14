package com.archive.api.business.read.dto;

import java.util.List;

/**
 * Пагинированный ответ поиска архивных рейсов.
 *
 * @param items список найденных рейсов
 * @param page номер текущей страницы (0-based)
 * @param size размер страницы
 * @param totalElements общее количество элементов
 * @param totalPages общее количество страниц
 */
public record ArchiveTripSearchResponse(
        List<ArchiveTripSearchItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
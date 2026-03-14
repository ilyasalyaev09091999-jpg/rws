package com.rws.api.rest.archive.dto;

/**
 * Результат синхронного импорта XLSX.
 *
 * @param fileName имя импортированного файла
 * @param totalRows общее количество строк в источнике
 * @param importedRows количество успешно импортированных строк
 * @param skippedRows количество пропущенных строк (валидация/дубликаты)
 * @param errorRows количество строк с ошибками импорта
 */
public record ArchiveImportResult(
        String fileName,
        int totalRows,
        int importedRows,
        int skippedRows,
        int errorRows
) {
}
package com.archive.api.business.importer;

/**
 * Статус асинхронной задачи импорта.
 *
 * @param jobId идентификатор задачи
 * @param status состояние задачи
 * @param fileName имя файла
 * @param totalRows общее количество строк (если известно)
 * @param importedRows количество успешно импортированных строк (если известно)
 * @param skippedRows количество пропущенных строк (если известно)
 * @param errorRows количество строк с ошибками (если известно)
 * @param errorMessage сообщение об ошибке при неуспешном завершении
 */
public record ArchiveImportJobStatus(
        String jobId,
        ArchiveImportJobState status,
        String fileName,
        Integer totalRows,
        Integer importedRows,
        Integer skippedRows,
        Integer errorRows,
        String errorMessage
) {
}
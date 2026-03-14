package com.rws.api.rest.archive.dto;

/**
 * Статус асинхронной задачи импорта XLSX.
 *
 * @param jobId идентификатор задачи
 * @param status состояние задачи (например, PENDING/RUNNING/DONE/FAILED)
 * @param fileName имя исходного файла
 * @param totalRows общее количество строк (если известно)
 * @param importedRows количество успешно импортированных строк (если известно)
 * @param skippedRows количество пропущенных строк (если известно)
 * @param errorRows количество строк с ошибками (если известно)
 * @param errorMessage сообщение об ошибке при неуспешном завершении
 */
public record ArchiveImportJobStatus(
        String jobId,
        String status,
        String fileName,
        Integer totalRows,
        Integer importedRows,
        Integer skippedRows,
        Integer errorRows,
        String errorMessage
) {
}
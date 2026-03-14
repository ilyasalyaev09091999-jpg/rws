package com.archive.api.grpc.mapper;

import com.archive.api.business.importer.ArchiveImportJobStatus;
import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import org.springframework.stereotype.Component;

/**
 * Маппер результатов импорта между внутренними DTO и protobuf.
 */
@Component
public class ArchiveGrpcImportMapper {

    /**
     * Маппит результат синхронного импорта в protobuf.
     *
     * @param result результат импорта
     * @return protobuf-ответ импорта
     */
    public ArchiveImportResultResponse toProto(ArchiveImportResult result) {
        return ArchiveImportResultResponse.newBuilder()
                .setFileName(result.fileName())
                .setTotalRows(result.totalRows())
                .setImportedRows(result.importedRows())
                .setSkippedRows(result.skippedRows())
                .setErrorRows(result.errorRows())
                .build();
    }

    /**
     * Маппит статус асинхронной задачи импорта в protobuf.
     *
     * @param status статус задачи
     * @return protobuf-ответ статуса
     */
    public ArchiveImportJobStatusResponse toProto(ArchiveImportJobStatus status) {
        ArchiveImportJobStatusResponse.Builder builder = ArchiveImportJobStatusResponse.newBuilder()
                .setJobId(status.jobId())
                .setStatus(status.status().name())
                .setFileName(nullToEmpty(status.fileName()))
                .setErrorMessage(nullToEmpty(status.errorMessage()));

        if (status.totalRows() != null) {
            builder.setTotalRows(status.totalRows());
        }
        if (status.importedRows() != null) {
            builder.setImportedRows(status.importedRows());
        }
        if (status.skippedRows() != null) {
            builder.setSkippedRows(status.skippedRows());
        }
        if (status.errorRows() != null) {
            builder.setErrorRows(status.errorRows());
        }

        return builder.build();
    }

    /**
     * Преобразует {@code null} в пустую строку для protobuf-полей.
     *
     * @param value исходное значение
     * @return пустая строка при {@code null}, иначе исходное значение
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
package com.rws.api.rest.archive.dto;

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

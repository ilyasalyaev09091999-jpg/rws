package com.archive.api.business.importer;

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

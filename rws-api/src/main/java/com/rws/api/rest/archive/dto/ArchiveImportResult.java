package com.rws.api.rest.archive.dto;

public record ArchiveImportResult(
        String fileName,
        int totalRows,
        int importedRows,
        int skippedRows,
        int errorRows
) {
}

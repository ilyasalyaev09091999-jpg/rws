package com.archive.api.business.importer;

public record ArchiveImportResult(
        String fileName,
        int totalRows,
        int importedRows,
        int skippedRows,
        int errorRows) {
}

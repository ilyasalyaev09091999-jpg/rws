package com.archive.api.grpc.mapper;

import com.archive.api.business.importer.ArchiveImportJobStatus;
import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import org.springframework.stereotype.Component;

@Component
public class ArchiveGrpcImportMapper {

    public ArchiveImportResultResponse toProto(ArchiveImportResult result) {
        return ArchiveImportResultResponse.newBuilder()
                .setFileName(result.fileName())
                .setTotalRows(result.totalRows())
                .setImportedRows(result.importedRows())
                .setSkippedRows(result.skippedRows())
                .setErrorRows(result.errorRows())
                .build();
    }

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

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

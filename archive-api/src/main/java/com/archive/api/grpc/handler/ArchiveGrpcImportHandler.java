package com.archive.api.grpc.handler;

import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.api.business.importer.ArchiveXlsxImportService;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ArchiveGrpcImportHandler {

    private final ArchiveXlsxImportService archiveXlsxImportService;

    public ArchiveImportResultResponse handle(ArchiveImportXlsxRequest request) {
        if (request.getContent().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = request.getFileName();
        if (fileName == null || fileName.isBlank()) {
            fileName = "unknown.xlsx";
        }

        MultipartFile multipartFile = new InMemoryMultipartFile(fileName, request.getContent().toByteArray());
        ArchiveImportResult result = archiveXlsxImportService.importFile(multipartFile);

        return ArchiveImportResultResponse.newBuilder()
                .setFileName(result.fileName())
                .setTotalRows(result.totalRows())
                .setImportedRows(result.importedRows())
                .setSkippedRows(result.skippedRows())
                .setErrorRows(result.errorRows())
                .build();
    }

    private static final class InMemoryMultipartFile implements MultipartFile {
        private final String filename;
        private final byte[] content;

        private InMemoryMultipartFile(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content == null ? 0 : content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}

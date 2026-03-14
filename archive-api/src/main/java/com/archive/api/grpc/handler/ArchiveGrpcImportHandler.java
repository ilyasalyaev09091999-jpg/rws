package com.archive.api.grpc.handler;

import com.archive.api.business.importer.ArchiveImportJobService;
import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.api.business.importer.ArchiveXlsxImportService;
import com.archive.api.business.importer.support.InMemoryMultipartFile;
import com.archive.api.grpc.mapper.ArchiveGrpcImportMapper;
import com.archive.api.grpc.mapper.ArchiveGrpcRequestMapper;
import com.archive.grpc.ArchiveImportJobStatusRequest;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Обработчик gRPC-запросов импорта архива.
 */
@Component
@RequiredArgsConstructor
public class ArchiveGrpcImportHandler {

    private final ArchiveXlsxImportService archiveXlsxImportService;
    private final ArchiveImportJobService archiveImportJobService;
    private final ArchiveGrpcImportMapper archiveGrpcImportMapper;
    private final ArchiveGrpcRequestMapper archiveGrpcRequestMapper;

    /**
     * Выполняет синхронный импорт и маппит результат в protobuf.
     *
     * @param request gRPC-запрос импорта
     * @return protobuf-ответ импорта
     */
    public ArchiveImportResultResponse handleSync(ArchiveImportXlsxRequest request) {
        if (request.getContent().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = archiveGrpcRequestMapper.normalizeFileName(request.getFileName());
        ArchiveImportResult result = archiveXlsxImportService.importFile(
                new InMemoryMultipartFile(fileName, request.getContent().toByteArray())
        );
        return archiveGrpcImportMapper.toProto(result);
    }

    /**
     * Запускает асинхронный импорт и возвращает начальный статус.
     *
     * @param request gRPC-запрос импорта
     * @return protobuf-ответ со статусом задачи
     */
    public ArchiveImportJobStatusResponse handleStartAsync(ArchiveImportXlsxRequest request) {
        String fileName = archiveGrpcRequestMapper.normalizeFileName(request.getFileName());
        return archiveGrpcImportMapper.toProto(
                archiveImportJobService.start(fileName, request.getContent().toByteArray())
        );
    }

    /**
     * Получает статус асинхронной задачи по {@code jobId}.
     *
     * @param request gRPC-запрос статуса
     * @return protobuf-ответ со статусом задачи
     */
    public ArchiveImportJobStatusResponse handleGetStatus(ArchiveImportJobStatusRequest request) {
        return archiveGrpcImportMapper.toProto(
                archiveImportJobService.getStatus(request.getJobId())
        );
    }
}
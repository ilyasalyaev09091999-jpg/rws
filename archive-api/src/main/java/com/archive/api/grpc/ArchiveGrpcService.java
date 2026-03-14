package com.archive.api.grpc;

import com.archive.api.grpc.handler.ArchiveGrpcImportHandler;
import com.archive.api.grpc.handler.ArchiveGrpcSearchHandler;
import com.archive.api.grpc.handler.ArchiveGrpcStatsHandler;
import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportJobStatusRequest;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveRouteStatsResponse;
import com.archive.grpc.ArchiveSearchRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * Реализация gRPC сервиса {@code ArchiveService} на стороне {@code archive-api}.
 */
@GrpcService
@RequiredArgsConstructor
public class ArchiveGrpcService extends ArchiveServiceGrpc.ArchiveServiceImplBase {

    private final ArchiveGrpcImportHandler importHandler;
    private final ArchiveGrpcSearchHandler searchHandler;
    private final ArchiveGrpcStatsHandler statsHandler;

    /**
     * Синхронный импорт XLSX через gRPC.
     *
     * @param request запрос импорта
     * @param responseObserver observer ответа
     */
    @Override
    public void importXlsx(ArchiveImportXlsxRequest request, StreamObserver<ArchiveImportResultResponse> responseObserver) {
        try {
            responseObserver.onNext(importHandler.handleSync(request));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive import failed").asRuntimeException());
        }
    }

    /**
     * Запуск асинхронного импорта XLSX через gRPC.
     *
     * @param request запрос импорта
     * @param responseObserver observer ответа
     */
    @Override
    public void startImportXlsx(ArchiveImportXlsxRequest request, StreamObserver<ArchiveImportJobStatusResponse> responseObserver) {
        try {
            responseObserver.onNext(importHandler.handleStartAsync(request));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive async import start failed").asRuntimeException());
        }
    }

    /**
     * Получение статуса асинхронного импорта.
     *
     * @param request запрос статуса
     * @param responseObserver observer ответа
     */
    @Override
    public void getImportJobStatus(ArchiveImportJobStatusRequest request, StreamObserver<ArchiveImportJobStatusResponse> responseObserver) {
        try {
            responseObserver.onNext(importHandler.handleGetStatus(request));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive import status failed").asRuntimeException());
        }
    }

    /**
     * Поиск архивных рейсов.
     *
     * @param request запрос поиска
     * @param responseObserver observer ответа
     */
    @Override
    public void searchTrips(ArchiveSearchRequest request, StreamObserver<com.archive.grpc.ArchiveTripSearchResponse> responseObserver) {
        try {
            responseObserver.onNext(searchHandler.handle(request));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive search failed").asRuntimeException());
        }
    }

    /**
     * Получение маршрутной статистики архива.
     *
     * @param request запрос аналитики
     * @param responseObserver observer ответа
     */
    @Override
    public void getRouteStats(ArchiveAnalyticsRequest request, StreamObserver<ArchiveRouteStatsResponse> responseObserver) {
        try {
            responseObserver.onNext(statsHandler.handle(request));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive analytics failed").asRuntimeException());
        }
    }
}
package com.rws.api.rest.archive.client;

import com.archive.grpc.ArchiveImportJobStatusRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import com.rws.api.rest.archive.dto.ArchiveImportJobStatus;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import com.rws.api.rest.archive.mapper.ArchiveGrpcClientMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * gRPC-клиент для взаимодействия с {@code archive-api}.
 *
 * <p>Инкапсулирует:
 * <ul>
 *   <li>вызовы gRPC-методов и таймауты,</li>
 *   <li>маппинг protobuf <-> REST DTO через {@link ArchiveGrpcClientMapper},</li>
 *   <li>преобразование gRPC-ошибок в исключения уровня {@code rws-api}.</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ArchiveApiClient {

    @GrpcClient("archive")
    private ArchiveServiceGrpc.ArchiveServiceBlockingStub stub;

    private final ArchiveGrpcClientMapper archiveGrpcClientMapper;

    /**
     * Синхронный импорт XLSX.
     *
     * @param file XLSX-файл из multipart запроса
     * @return статистика импорта по файлу
     * @throws IllegalArgumentException если файл пустой или не читается
     * @throws ArchiveApiUnavailableException при транспортных ошибках или таймауте
     */
    public ArchiveImportResult importXlsx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            var request = archiveGrpcClientMapper.toProtoImportRequest(file.getOriginalFilename(), file.getBytes());
            var response = stub.withDeadlineAfter(300, TimeUnit.SECONDS).importXlsx(request);
            return archiveGrpcClientMapper.fromProto(response);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read file", ex);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    /**
     * Запускает асинхронный импорт XLSX и возвращает начальный статус задачи.
     *
     * @param file XLSX-файл из multipart запроса
     * @return статус задачи с {@code jobId} и начальными счетчиками
     * @throws IllegalArgumentException если файл пустой или не читается
     * @throws ArchiveApiUnavailableException при транспортных ошибках или таймауте
     */
    public ArchiveImportJobStatus startImportXlsx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            var request = archiveGrpcClientMapper.toProtoImportRequest(file.getOriginalFilename(), file.getBytes());
            var response = stub.withDeadlineAfter(20, TimeUnit.SECONDS).startImportXlsx(request);
            return archiveGrpcClientMapper.fromProto(response);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read file", ex);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    /**
     * Возвращает статус асинхронной задачи импорта.
     *
     * @param jobId идентификатор задачи импорта
     * @return актуальный статус задачи со счетчиками и возможным сообщением об ошибке
     * @throws IllegalArgumentException если {@code jobId} пустой
     * @throws ArchiveApiUnavailableException при транспортных ошибках или таймауте
     */
    public ArchiveImportJobStatus getImportJobStatus(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId is empty");
        }

        try {
            var response = stub.withDeadlineAfter(20, TimeUnit.SECONDS)
                    .getImportJobStatus(ArchiveImportJobStatusRequest.newBuilder().setJobId(jobId).build());
            return archiveGrpcClientMapper.fromProto(response);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    /**
     * Поиск архивных рейсов по фильтрам.
     *
     * @param departurePoint опциональная точка отправления (город/порт)
     * @param destinationPoint опциональная точка назначения (город/порт)
     * @param dateFrom опциональная нижняя граница даты отправления (включительно)
     * @param dateTo опциональная верхняя граница даты отправления (включительно)
     * @param page номер страницы (0-based)
     * @param size размер страницы
     * @return результат поиска с элементами и метаданными пагинации
     * @throws ArchiveApiUnavailableException при транспортных ошибках или таймауте
     */
    public ArchiveTripSearchResponse search(String departurePoint,
                                            String destinationPoint,
                                            LocalDate dateFrom,
                                            LocalDate dateTo,
                                            int page,
                                            int size) {
        var request = archiveGrpcClientMapper.toProtoSearchRequest(
                departurePoint,
                destinationPoint,
                dateFrom,
                dateTo,
                page,
                size
        );

        try {
            var response = stub.withDeadlineAfter(60, TimeUnit.SECONDS).searchTrips(request);
            return archiveGrpcClientMapper.fromProto(response);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    /**
     * Возвращает агрегированную статистику по маршрутам.
     *
     * @param departurePoint опциональная точка отправления (город/порт)
     * @param destinationPoint опциональная точка назначения (город/порт)
     * @param month опциональный месяц отправления (1..12) или {@code null}
     * @return список статистических элементов по маршрутам
     * @throws ArchiveApiUnavailableException при транспортных ошибках или таймауте
     */
    public List<ArchiveRouteStatsItem> analytics(String departurePoint,
                                                 String destinationPoint,
                                                 Integer month) {
        var request = archiveGrpcClientMapper.toProtoAnalyticsRequest(departurePoint, destinationPoint, month);

        try {
            var response = stub.withDeadlineAfter(60, TimeUnit.SECONDS).getRouteStats(request);
            return archiveGrpcClientMapper.fromProto(response);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    /**
     * Преобразует gRPC-ошибки в исключения уровня {@code rws-api}.
     *
     * @param ex gRPC-исключение от вызова stub
     * @return runtime-исключение для REST-слоя
     */
    private RuntimeException mapGrpcError(StatusRuntimeException ex) {
        return switch (ex.getStatus().getCode()) {
            case INVALID_ARGUMENT, NOT_FOUND -> new IllegalArgumentException(ex.getStatus().getDescription());
            case DEADLINE_EXCEEDED -> new ArchiveApiUnavailableException("Archive service timeout", ex);
            default -> new ArchiveApiUnavailableException("Archive API unavailable", ex);
        };
    }
}
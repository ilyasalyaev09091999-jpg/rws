package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchiveTripSearchService;
import com.archive.api.grpc.mapper.ArchiveGrpcRequestMapper;
import com.archive.api.grpc.mapper.ArchiveGrpcSearchMapper;
import com.archive.grpc.ArchiveSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Обработчик gRPC-запросов поиска по архиву.
 */
@Component
@RequiredArgsConstructor
public class ArchiveGrpcSearchHandler {

    private final ArchiveTripSearchService archiveTripSearchService;
    private final ArchiveGrpcSearchMapper archiveGrpcSearchMapper;
    private final ArchiveGrpcRequestMapper archiveGrpcRequestMapper;

    /**
     * Выполняет поиск и маппит результат в protobuf.
     *
     * @param request gRPC-запрос поиска
     * @return protobuf-ответ поиска
     */
    public com.archive.grpc.ArchiveTripSearchResponse handle(ArchiveSearchRequest request) {
        String departurePoint = archiveGrpcRequestMapper.nullIfBlank(request.getDeparturePoint());
        String destinationPoint = archiveGrpcRequestMapper.nullIfBlank(request.getDestinationPoint());
        LocalDate dateFrom = archiveGrpcRequestMapper.parseDate(request.getDateFrom());
        LocalDate dateTo = archiveGrpcRequestMapper.parseDate(request.getDateTo());

        return archiveGrpcSearchMapper.toProto(
                archiveTripSearchService.search(
                        departurePoint,
                        destinationPoint,
                        dateFrom,
                        dateTo,
                        request.getPage(),
                        request.getSize()
                )
        );
    }
}
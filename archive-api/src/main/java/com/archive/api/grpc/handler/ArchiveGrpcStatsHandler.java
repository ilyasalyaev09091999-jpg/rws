package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchiveRouteStatsService;
import com.archive.api.grpc.mapper.ArchiveGrpcRequestMapper;
import com.archive.api.grpc.mapper.ArchiveGrpcStatsMapper;
import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveRouteStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Обработчик gRPC-запросов маршрутной статистики.
 */
@Component
@RequiredArgsConstructor
public class ArchiveGrpcStatsHandler {

    private final ArchiveRouteStatsService archiveRouteStatsService;
    private final ArchiveGrpcStatsMapper archiveGrpcStatsMapper;
    private final ArchiveGrpcRequestMapper archiveGrpcRequestMapper;

    /**
     * Возвращает агрегированную статистику и маппит ее в protobuf.
     *
     * @param request gRPC-запрос аналитики
     * @return protobuf-ответ со статистикой
     */
    public ArchiveRouteStatsResponse handle(ArchiveAnalyticsRequest request) {
        String departurePoint = archiveGrpcRequestMapper.nullIfBlank(request.getDeparturePoint());
        String destinationPoint = archiveGrpcRequestMapper.nullIfBlank(request.getDestinationPoint());
        Integer month = archiveGrpcRequestMapper.monthOrNull(request.getMonth());

        return archiveGrpcStatsMapper.toProto(
                archiveRouteStatsService.stats(departurePoint, destinationPoint, month)
        );
    }
}
package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchivePointSuggestionsService;
import com.archive.grpc.ArchivePointSuggestionsRequest;
import com.archive.grpc.ArchivePointSuggestionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles gRPC requests for archive point suggestions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveGrpcPointSuggestionsHandler {

    private final ArchivePointSuggestionsService archivePointSuggestionsService;

    /**
     * Формирует protobuf-ответ со списком архивных точек для автоподсказок.
     *
     * @param request gRPC-запрос на получение списка точек
     * @return protobuf-ответ со всеми доступными точками архива
     */
    public ArchivePointSuggestionsResponse handle(ArchivePointSuggestionsRequest request) {
        log.info("Handling archive point suggestions request");
        var points = archivePointSuggestionsService.getPoints();
        log.info("Archive point suggestions prepared. pointsCount={}", points.size());

        return ArchivePointSuggestionsResponse.newBuilder()
                .addAllPoints(points)
                .build();
    }
}

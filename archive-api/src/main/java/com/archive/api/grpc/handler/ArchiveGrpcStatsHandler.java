package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchiveRouteStatsService;
import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveRouteStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArchiveGrpcStatsHandler {

    private final ArchiveRouteStatsService archiveRouteStatsService;

    public ArchiveRouteStatsResponse handle(ArchiveAnalyticsRequest request) {
        String departurePoint = nullIfBlank(request.getDeparturePoint());
        String destinationPoint = nullIfBlank(request.getDestinationPoint());
        Integer month = request.getMonth() > 0 ? request.getMonth() : null;

        List<ArchiveRouteStatsItem> stats = archiveRouteStatsService.stats(departurePoint, destinationPoint, month);

        ArchiveRouteStatsResponse.Builder responseBuilder = ArchiveRouteStatsResponse.newBuilder();
        for (ArchiveRouteStatsItem item : stats) {
            responseBuilder.addItems(
                    com.archive.grpc.ArchiveRouteStatsItem.newBuilder()
                            .setDeparturePoint(nullToEmpty(item.departurePoint()))
                            .setDestinationPoint(nullToEmpty(item.destinationPoint()))
                            .setDepartureMonth(item.departureMonth() == null ? 0 : item.departureMonth())
                            .setTripsCount(item.tripsCount() == null ? 0 : item.tripsCount())
                            .setMinDays(item.minDays() == null ? 0 : item.minDays())
                            .setMaxDays(item.maxDays() == null ? 0 : item.maxDays())
                            .setAvgDays(toDecimalString(item.avgDays()))
                            .setP50Days(toDecimalString(item.p50Days()))
                            .setP80Days(toDecimalString(item.p80Days()))
                            .setUncertaintyDays(toDecimalString(item.uncertaintyDays()))
                            .build()
            );
        }

        return responseBuilder.build();
    }

    private String toDecimalString(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String nullIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}

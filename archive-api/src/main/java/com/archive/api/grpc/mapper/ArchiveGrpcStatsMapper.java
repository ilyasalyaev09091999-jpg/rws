package com.archive.api.grpc.mapper;

import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import com.archive.grpc.ArchiveRouteStatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ArchiveGrpcStatsMapper {

    public ArchiveRouteStatsResponse toProto(List<ArchiveRouteStatsItem> stats) {
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
}

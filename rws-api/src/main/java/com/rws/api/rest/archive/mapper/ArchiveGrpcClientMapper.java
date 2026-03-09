package com.rws.api.rest.archive.mapper;

import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveSearchRequest;
import com.rws.api.rest.archive.dto.ArchiveImportJobStatus;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Mapper между protobuf-контрактом {@code archive-proto} и REST DTO слоя {@code rws-api}.
 */
@Component
public class ArchiveGrpcClientMapper {

    /**
     * Формирует protobuf-запрос импорта файла.
     */
    public ArchiveImportXlsxRequest toProtoImportRequest(String fileName, byte[] content) {
        return ArchiveImportXlsxRequest.newBuilder()
                .setFileName(fileName == null ? "unknown.xlsx" : fileName)
                .setContent(com.google.protobuf.ByteString.copyFrom(content))
                .build();
    }

    /**
     * Формирует protobuf-запрос поиска рейсов.
     */
    public ArchiveSearchRequest toProtoSearchRequest(String departurePoint,
                                                     String destinationPoint,
                                                     LocalDate dateFrom,
                                                     LocalDate dateTo,
                                                     int page,
                                                     int size) {
        return ArchiveSearchRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setDateFrom(dateFrom == null ? "" : dateFrom.toString())
                .setDateTo(dateTo == null ? "" : dateTo.toString())
                .setPage(page)
                .setSize(size)
                .build();
    }

    /**
     * Формирует protobuf-запрос аналитики.
     */
    public ArchiveAnalyticsRequest toProtoAnalyticsRequest(String departurePoint,
                                                           String destinationPoint,
                                                           Integer month) {
        return ArchiveAnalyticsRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setMonth(month == null ? 0 : month)
                .build();
    }

    /**
     * Маппинг ответа синхронного импорта в REST DTO.
     */
    public ArchiveImportResult fromProto(ArchiveImportResultResponse response) {
        return new ArchiveImportResult(
                response.getFileName(),
                response.getTotalRows(),
                response.getImportedRows(),
                response.getSkippedRows(),
                response.getErrorRows()
        );
    }

    /**
     * Маппинг статуса async-импорта в REST DTO.
     */
    public ArchiveImportJobStatus fromProto(ArchiveImportJobStatusResponse response) {
        return new ArchiveImportJobStatus(
                response.getJobId(),
                response.getStatus(),
                emptyToNull(response.getFileName()),
                response.getTotalRows(),
                response.getImportedRows(),
                response.getSkippedRows(),
                response.getErrorRows(),
                emptyToNull(response.getErrorMessage())
        );
    }

    /**
     * Маппинг protobuf-ответа поиска в REST DTO.
     */
    public ArchiveTripSearchResponse fromProto(com.archive.grpc.ArchiveTripSearchResponse response) {
        List<ArchiveTripSearchItem> items = response.getItemsList().stream()
                .map(item -> new ArchiveTripSearchItem(
                        item.hasId() ? item.getId() : null,
                        emptyToNull(item.getVoyageName()),
                        emptyToNull(item.getTripType()),
                        emptyToNull(item.getTugName()),
                        emptyToNull(item.getDeparturePoint()),
                        emptyToNull(item.getDestinationPoint()),
                        parseDateOrNull(item.getDepartureDate()),
                        parseDateOrNull(item.getArrivalDate()),
                        item.hasDurationDays() ? item.getDurationDays() : null,
                        emptyToNull(item.getCargoType()),
                        parseDecimalOrNull(item.getCargoAmount()),
                        parseDecimalOrNull(item.getDraftM()),
                        emptyToNull(item.getCounterpartyName()),
                        emptyToNull(item.getCounterpartyInn()),
                        emptyToNull(item.getFlag()),
                        item.hasUnitsCount() ? item.getUnitsCount() : null,
                        emptyToNull(item.getRegionFrom()),
                        emptyToNull(item.getRegionTo())
                ))
                .toList();

        return new ArchiveTripSearchResponse(
                items,
                response.getPage(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages()
        );
    }

    /**
     * Маппинг protobuf-ответа аналитики в REST DTO.
     */
    public List<ArchiveRouteStatsItem> fromProto(com.archive.grpc.ArchiveRouteStatsResponse response) {
        return response.getItemsList().stream()
                .map(item -> new ArchiveRouteStatsItem(
                        emptyToNull(item.getDeparturePoint()),
                        emptyToNull(item.getDestinationPoint()),
                        item.getDepartureMonth(),
                        item.getTripsCount(),
                        item.getMinDays(),
                        item.getMaxDays(),
                        parseDecimalOrNull(item.getAvgDays()),
                        parseDecimalOrNull(item.getP50Days()),
                        parseDecimalOrNull(item.getP80Days()),
                        parseDecimalOrNull(item.getUncertaintyDays())
                ))
                .toList();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private LocalDate parseDateOrNull(String value) {
        String normalized = emptyToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private BigDecimal parseDecimalOrNull(String value) {
        String normalized = emptyToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

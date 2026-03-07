package com.rws.api.rest.archive.client;

import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveSearchRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ArchiveApiClient {

    @GrpcClient("archive")
    private ArchiveServiceGrpc.ArchiveServiceBlockingStub stub;

    public ArchiveImportResult importXlsx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            ArchiveImportXlsxRequest request = ArchiveImportXlsxRequest.newBuilder()
                    .setFileName(file.getOriginalFilename() == null ? "unknown.xlsx" : file.getOriginalFilename())
                    .setContent(com.google.protobuf.ByteString.copyFrom(file.getBytes()))
                    .build();

            com.archive.grpc.ArchiveImportResultResponse response = stub.withDeadlineAfter(300, TimeUnit.SECONDS).importXlsx(request);
            return new ArchiveImportResult(
                    response.getFileName(),
                    response.getTotalRows(),
                    response.getImportedRows(),
                    response.getSkippedRows(),
                    response.getErrorRows()
            );
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read file", ex);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    public ArchiveTripSearchResponse search(String departurePoint,
                                            String destinationPoint,
                                            LocalDate dateFrom,
                                            LocalDate dateTo,
                                            int page,
                                            int size) {
        ArchiveSearchRequest request = ArchiveSearchRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setDateFrom(dateFrom == null ? "" : dateFrom.toString())
                .setDateTo(dateTo == null ? "" : dateTo.toString())
                .setPage(page)
                .setSize(size)
                .build();

        try {
            com.archive.grpc.ArchiveTripSearchResponse response = stub.withDeadlineAfter(60, TimeUnit.SECONDS).searchTrips(request);

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
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    public List<ArchiveRouteStatsItem> analytics(String departurePoint,
                                                 String destinationPoint,
                                                 Integer month) {
        ArchiveAnalyticsRequest request = ArchiveAnalyticsRequest.newBuilder()
                .setDeparturePoint(nullToEmpty(departurePoint))
                .setDestinationPoint(nullToEmpty(destinationPoint))
                .setMonth(month == null ? 0 : month)
                .build();

        try {
            com.archive.grpc.ArchiveRouteStatsResponse response = stub.withDeadlineAfter(60, TimeUnit.SECONDS).getRouteStats(request);
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
        } catch (StatusRuntimeException ex) {
            throw mapGrpcError(ex);
        }
    }

    private RuntimeException mapGrpcError(StatusRuntimeException ex) {
        return switch (ex.getStatus().getCode()) {
            case INVALID_ARGUMENT -> new IllegalArgumentException(ex.getStatus().getDescription());
            case DEADLINE_EXCEEDED -> new ArchiveApiUnavailableException("Archive service timeout", ex);
            default -> new ArchiveApiUnavailableException("Archive API unavailable", ex);
        };
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

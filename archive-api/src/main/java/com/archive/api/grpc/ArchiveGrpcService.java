package com.archive.api.grpc;

import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.api.business.importer.ArchiveXlsxImportService;
import com.archive.api.business.read.ArchiveReadService;
import com.archive.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.archive.api.rest.archive.dto.ArchiveTripSearchItem;
import com.archive.api.rest.archive.dto.ArchiveTripSearchResponse;
import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveRouteStatsResponse;
import com.archive.grpc.ArchiveSearchRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class ArchiveGrpcService extends ArchiveServiceGrpc.ArchiveServiceImplBase {

    private final ArchiveXlsxImportService archiveXlsxImportService;
    private final ArchiveReadService archiveReadService;

    @Override
    public void importXlsx(ArchiveImportXlsxRequest request, StreamObserver<ArchiveImportResultResponse> responseObserver) {
        try {
            if (request.getContent().isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String fileName = nullIfBlank(request.getFileName());
            if (fileName == null) {
                fileName = "unknown.xlsx";
            }

            MultipartFile multipartFile = new InMemoryMultipartFile(fileName, request.getContent().toByteArray());
            ArchiveImportResult result = archiveXlsxImportService.importFile(multipartFile);

            ArchiveImportResultResponse response = ArchiveImportResultResponse.newBuilder()
                    .setFileName(result.fileName())
                    .setTotalRows(result.totalRows())
                    .setImportedRows(result.importedRows())
                    .setSkippedRows(result.skippedRows())
                    .setErrorRows(result.errorRows())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive import failed").asRuntimeException());
        }
    }

    @Override
    public void searchTrips(ArchiveSearchRequest request, StreamObserver<com.archive.grpc.ArchiveTripSearchResponse> responseObserver) {
        try {
            String departurePoint = nullIfBlank(request.getDeparturePoint());
            String destinationPoint = nullIfBlank(request.getDestinationPoint());
            LocalDate dateFrom = parseDateOrNull(request.getDateFrom());
            LocalDate dateTo = parseDateOrNull(request.getDateTo());

            ArchiveTripSearchResponse result = archiveReadService.search(
                    departurePoint,
                    destinationPoint,
                    dateFrom,
                    dateTo,
                    request.getPage(),
                    request.getSize()
            );

            com.archive.grpc.ArchiveTripSearchResponse.Builder responseBuilder = com.archive.grpc.ArchiveTripSearchResponse.newBuilder()
                    .setPage(result.page())
                    .setSize(result.size())
                    .setTotalElements(result.totalElements())
                    .setTotalPages(result.totalPages());

            for (ArchiveTripSearchItem item : result.items()) {
                com.archive.grpc.ArchiveTripItem.Builder itemBuilder = com.archive.grpc.ArchiveTripItem.newBuilder()
                        .setVoyageName(nullToEmpty(item.voyageName()))
                        .setTripType(nullToEmpty(item.tripType()))
                        .setTugName(nullToEmpty(item.tugName()))
                        .setDeparturePoint(nullToEmpty(item.departurePoint()))
                        .setDestinationPoint(nullToEmpty(item.destinationPoint()))
                        .setDepartureDate(toDateString(item.departureDate()))
                        .setArrivalDate(toDateString(item.arrivalDate()))
                        .setCargoType(nullToEmpty(item.cargoType()))
                        .setCargoAmount(toDecimalString(item.cargoAmount()))
                        .setDraftM(toDecimalString(item.draftM()))
                        .setCounterpartyName(nullToEmpty(item.counterpartyName()))
                        .setCounterpartyInn(nullToEmpty(item.counterpartyInn()))
                        .setFlag(nullToEmpty(item.flag()))
                        .setRegionFrom(nullToEmpty(item.regionFrom()))
                        .setRegionTo(nullToEmpty(item.regionTo()));

                if (item.id() != null) {
                    itemBuilder.setId(item.id());
                }
                if (item.durationDays() != null) {
                    itemBuilder.setDurationDays(item.durationDays());
                }
                if (item.unitsCount() != null) {
                    itemBuilder.setUnitsCount(item.unitsCount());
                }

                responseBuilder.addItems(itemBuilder.build());
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive search failed").asRuntimeException());
        }
    }

    @Override
    public void getRouteStats(ArchiveAnalyticsRequest request, StreamObserver<ArchiveRouteStatsResponse> responseObserver) {
        try {
            String departurePoint = nullIfBlank(request.getDeparturePoint());
            String destinationPoint = nullIfBlank(request.getDestinationPoint());
            Integer month = request.getMonth() > 0 ? request.getMonth() : null;

            List<ArchiveRouteStatsItem> stats = archiveReadService.stats(departurePoint, destinationPoint, month);

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

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Archive analytics failed").asRuntimeException());
        }
    }

    private LocalDate parseDateOrNull(String value) {
        String normalized = nullIfBlank(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date: " + value);
        }
    }

    private String toDateString(LocalDate value) {
        return value == null ? "" : value.toString();
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

    private static final class InMemoryMultipartFile implements MultipartFile {
        private final String filename;
        private final byte[] content;

        private InMemoryMultipartFile(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content == null ? 0 : content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}

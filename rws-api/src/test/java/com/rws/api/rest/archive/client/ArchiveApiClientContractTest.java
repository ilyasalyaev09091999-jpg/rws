package com.rws.api.rest.archive.client;

import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportJobStatusRequest;
import com.archive.grpc.ArchiveImportJobStatusResponse;
import com.archive.grpc.ArchiveImportResultResponse;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveRouteStatsResponse;
import com.archive.grpc.ArchiveSearchRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import com.rws.api.rest.archive.mapper.ArchiveGrpcClientMapper;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveApiClientContractTest {

    private Server server;
    private ManagedChannel channel;

    @BeforeEach
    void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new FakeArchiveService())
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void clientMapsSearchAndStatsAndAsyncImport() throws Exception {
        ArchiveApiClient client = new ArchiveApiClient(new ArchiveGrpcClientMapper());
        Field stubField = ArchiveApiClient.class.getDeclaredField("stub");
        stubField.setAccessible(true);
        stubField.set(client, ArchiveServiceGrpc.newBlockingStub(channel));

        var search = client.search("A", "B", LocalDate.parse("2026-03-01"), LocalDate.parse("2026-03-02"), 0, 20);
        assertEquals(1, search.items().size());
        assertEquals("A", search.items().get(0).departurePoint());

        var stats = client.analytics("A", "B", null);
        assertEquals(1, stats.size());
        assertEquals(7L, stats.get(0).tripsCount());

        var asyncJob = client.startImportXlsx(new MockMultipartFile("file", "demo.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "x".getBytes()));
        assertEquals("RUNNING", asyncJob.status());

        var status = client.getImportJobStatus("job-1");
        assertEquals("DONE", status.status());

        var sync = client.importXlsx(new MockMultipartFile("file", "demo.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "x".getBytes()));
        assertEquals(10, sync.totalRows());
    }

    private static class FakeArchiveService extends ArchiveServiceGrpc.ArchiveServiceImplBase {

        @Override
        public void importXlsx(ArchiveImportXlsxRequest request, StreamObserver<ArchiveImportResultResponse> responseObserver) {
            responseObserver.onNext(ArchiveImportResultResponse.newBuilder()
                    .setFileName(request.getFileName())
                    .setTotalRows(10)
                    .setImportedRows(8)
                    .setSkippedRows(1)
                    .setErrorRows(1)
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void startImportXlsx(ArchiveImportXlsxRequest request, StreamObserver<ArchiveImportJobStatusResponse> responseObserver) {
            responseObserver.onNext(ArchiveImportJobStatusResponse.newBuilder()
                    .setJobId("job-1")
                    .setStatus("RUNNING")
                    .setFileName(request.getFileName())
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void getImportJobStatus(ArchiveImportJobStatusRequest request, StreamObserver<ArchiveImportJobStatusResponse> responseObserver) {
            responseObserver.onNext(ArchiveImportJobStatusResponse.newBuilder()
                    .setJobId(request.getJobId())
                    .setStatus("DONE")
                    .setTotalRows(10)
                    .setImportedRows(9)
                    .setSkippedRows(1)
                    .setErrorRows(0)
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void searchTrips(ArchiveSearchRequest request, StreamObserver<com.archive.grpc.ArchiveTripSearchResponse> responseObserver) {
            responseObserver.onNext(com.archive.grpc.ArchiveTripSearchResponse.newBuilder()
                    .setPage(request.getPage())
                    .setSize(request.getSize())
                    .setTotalElements(1)
                    .setTotalPages(1)
                    .addItems(com.archive.grpc.ArchiveTripItem.newBuilder()
                            .setId(1)
                            .setDeparturePoint("A")
                            .setDestinationPoint("B")
                            .setDepartureDate("2026-03-01")
                            .setArrivalDate("2026-03-02")
                            .setCargoAmount("1.5")
                            .build())
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void getRouteStats(ArchiveAnalyticsRequest request, StreamObserver<ArchiveRouteStatsResponse> responseObserver) {
            responseObserver.onNext(ArchiveRouteStatsResponse.newBuilder()
                    .addItems(com.archive.grpc.ArchiveRouteStatsItem.newBuilder()
                            .setDeparturePoint("A")
                            .setDestinationPoint("B")
                            .setTripsCount(7)
                            .setAvgDays("2.2")
                            .build())
                    .build());
            responseObserver.onCompleted();
        }
    }
}

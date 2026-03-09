package com.archive.api.grpc;

import com.archive.api.business.read.dto.ArchiveRouteStatsItem;
import com.archive.api.business.read.dto.ArchiveTripSearchResponse;
import com.archive.api.grpc.handler.ArchiveGrpcImportHandler;
import com.archive.api.grpc.handler.ArchiveGrpcSearchHandler;
import com.archive.api.grpc.handler.ArchiveGrpcStatsHandler;
import com.archive.grpc.ArchiveAnalyticsRequest;
import com.archive.grpc.ArchiveImportXlsxRequest;
import com.archive.grpc.ArchiveSearchRequest;
import com.archive.grpc.ArchiveServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArchiveGrpcServiceContractTest {

    private Server server;
    private ManagedChannel channel;
    private ArchiveServiceGrpc.ArchiveServiceBlockingStub stub;

    private ArchiveGrpcImportHandler importHandler;
    private ArchiveGrpcSearchHandler searchHandler;
    private ArchiveGrpcStatsHandler statsHandler;

    @BeforeEach
    void setUp() throws Exception {
        importHandler = mock(ArchiveGrpcImportHandler.class);
        searchHandler = mock(ArchiveGrpcSearchHandler.class);
        statsHandler = mock(ArchiveGrpcStatsHandler.class);

        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new ArchiveGrpcService(importHandler, searchHandler, statsHandler))
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        stub = ArchiveServiceGrpc.newBlockingStub(channel);
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
    void searchTripsReturnsMappedResponse() {
        when(searchHandler.handle(any())).thenReturn(
                com.archive.grpc.ArchiveTripSearchResponse.newBuilder()
                        .setPage(0)
                        .setSize(20)
                        .setTotalElements(1)
                        .setTotalPages(1)
                        .addItems(com.archive.grpc.ArchiveTripItem.newBuilder()
                                .setDeparturePoint("A")
                                .setDestinationPoint("B")
                                .setDepartureDate("2026-03-01")
                                .setArrivalDate("2026-03-02")
                                .build())
                        .build()
        );

        var response = stub.searchTrips(ArchiveSearchRequest.newBuilder().setPage(0).setSize(20).build());
        assertEquals(1, response.getItemsCount());
        assertEquals("A", response.getItems(0).getDeparturePoint());
    }

    @Test
    void getRouteStatsReturnsData() {
        when(statsHandler.handle(any())).thenReturn(
                com.archive.grpc.ArchiveRouteStatsResponse.newBuilder()
                        .addItems(com.archive.grpc.ArchiveRouteStatsItem.newBuilder()
                                .setDeparturePoint("A")
                                .setDestinationPoint("B")
                                .setTripsCount(5)
                                .setAvgDays("2.5")
                                .build())
                        .build()
        );

        var response = stub.getRouteStats(ArchiveAnalyticsRequest.newBuilder().build());
        assertEquals(1, response.getItemsCount());
        assertEquals(5, response.getItems(0).getTripsCount());
    }

    @Test
    void importXlsxReturnsInvalidArgumentOnValidationError() {
        when(importHandler.handleSync(any())).thenThrow(new IllegalArgumentException("File is empty"));

        StatusRuntimeException ex = assertThrows(
                StatusRuntimeException.class,
                () -> stub.importXlsx(ArchiveImportXlsxRequest.newBuilder().build())
        );

        assertTrue(ex.getStatus().getCode().name().equals("INVALID_ARGUMENT"));
    }
}

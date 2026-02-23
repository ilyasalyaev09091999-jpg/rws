package com.rws.api.rest.route.grpcclient;

import com.google.protobuf.Timestamp;
import com.route.grpc.RouteServiceGrpc;
import com.rws.api.rest.route.controller.error.RouteNotFoundException;
import com.rws.api.rest.route.dto.LockDto;
import com.rws.api.rest.route.dto.RouteFinderRequest;
import com.rws.api.rest.route.dto.RouteFinderResponse;
import com.rws.api.rest.route.dto.RouteNode;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RouteGrpcClient {

    @GrpcClient("route")
    private RouteServiceGrpc.RouteServiceBlockingStub stub;

    public RouteFinderResponse findRoute(RouteFinderRequest request) throws RouteNotFoundException {
        // 1️⃣ REST DTO → Proto
        com.route.grpc.RouteFinderRequest protoRequest = com.route.grpc.RouteFinderRequest.newBuilder()
                .setStartLongitude(request.startLongitude())
                .setStartLatitude(request.startLatitude())
                .setEndLongitude(request.endLongitude())
                .setEndLatitude(request.endLatitude())
                .setDepartureTime(toTimestamp(request.departureTime()))
                .setSpeed(request.speed())
                .build();

        // 2️⃣ Вызов gRPC с обработкой ошибок
        com.route.grpc.RouteFinderResponse protoResponse;
        try {
            protoResponse = stub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .findRoute(protoRequest);

        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND ->
                        throw new RouteNotFoundException("Route not found");
                case INVALID_ARGUMENT ->
                        throw new IllegalArgumentException(e.getStatus().getDescription());
                case DEADLINE_EXCEEDED ->
                        throw new RuntimeException("Route service timeout");
                default ->
                        throw new RuntimeException("Route service error", e);
            }
        }

        // 3️⃣ Proto → REST DTO
        return mapFromProto(protoResponse);
    }

    private RouteFinderResponse mapFromProto(com.route.grpc.RouteFinderResponse response) {
        return new RouteFinderResponse(
                response.getDuration(),
                toLocalDateTime(response.getArrivalDateTime()),
                response.getTotalDistance(),
                response.getRouteList().stream()
                        .map(r -> new RouteNode(r.getSeq(), r.getNodeId(), r.getLat(), r.getLon(), r.getCost()))
                        .toList(),
                response.getRouteLocksList().stream()
                        .map(l -> new LockDto(l.getId(), l.getName(), l.getLatitude(), l.getLongitude(), new HashSet<>(l.getNodeIdsList())))
                        .toList()
        );
    }

    private Timestamp toTimestamp(LocalDateTime time) {
        return Timestamp.newBuilder()
                .setSeconds(time.toEpochSecond(ZoneOffset.UTC))
                .setNanos(time.getNano())
                .build();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos(),
                ZoneOffset.UTC
        );
    }
}

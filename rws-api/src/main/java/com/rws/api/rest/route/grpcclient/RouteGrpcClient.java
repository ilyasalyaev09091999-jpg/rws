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
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * gRPC-клиент для обращения к сервису {@code route-api} из {@code rws-api}.
 * <p>
 * Отвечает за:
 * </p>
 * <ul>
 *   <li>преобразование REST DTO в protobuf-запрос;</li>
 *   <li>выполнение gRPC-вызова с deadline;</li>
 *   <li>маппинг protobuf-ответа в REST DTO;</li>
 *   <li>трансляцию gRPC-ошибок в исключения уровня {@code rws-api}.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteGrpcClient {

    @GrpcClient("route")
    private RouteServiceGrpc.RouteServiceBlockingStub stub;

    /**
     * Вызывает удалённый расчёт маршрута в {@code route-api}.
     *
     * @param request входной запрос на поиск маршрута.
     * @return рассчитанный маршрут в REST-представлении.
     * @throws RouteNotFoundException если удалённый сервис вернул статус
     *                                {@code NOT_FOUND}.
     * @throws IllegalArgumentException если удалённый сервис вернул
     *                                  {@code INVALID_ARGUMENT}.
     * @throws RuntimeException при таймауте или прочей ошибке gRPC.
     */
    public RouteFinderResponse findRoute(RouteFinderRequest request) throws RouteNotFoundException {
        com.route.grpc.RouteFinderRequest protoRequest = com.route.grpc.RouteFinderRequest.newBuilder()
                .setStartLongitude(request.startLongitude())
                .setStartLatitude(request.startLatitude())
                .setEndLongitude(request.endLongitude())
                .setEndLatitude(request.endLatitude())
                .setDepartureTime(toTimestamp(request.departureTime()))
                .setSpeed(request.speed())
                .build();
        log.info("Parsed request: {}", protoRequest);

        com.route.grpc.RouteFinderResponse protoResponse;
        try {
            protoResponse = stub
                    .withDeadlineAfter(300, TimeUnit.SECONDS)
                    .findRoute(protoRequest);

        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> {
                    log.error("Find route error: NOT_FOUND");
                    throw new RouteNotFoundException("Route not found");
                }
                case INVALID_ARGUMENT -> {
                    log.error("Find route error: INVALID_ARGUMENT");
                    throw new IllegalArgumentException(e.getStatus().getDescription());
                }
                case DEADLINE_EXCEEDED -> {
                    log.error("Find route error: DEADLINE_EXCEEDED");
                    throw new RuntimeException("Route service timeout");
                }
                default -> {
                    log.info("Find route error: UNKNOWN");
                    throw new RuntimeException("Route service error", e);
                }

            }
        }

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
                        .map(l -> new LockDto(l.getName()))
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

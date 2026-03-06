package com.refdata.api.grpc;

import com.refdata.api.domain.api.PortService;
import com.refdata.api.domain.entities.route_api.PortForRoute;
import com.refdata.api.domain.entities.rws_api.PortForRws;
import com.refdata.grpc.Empty;
import com.refdata.grpc.PortListForRoute;
import com.refdata.grpc.PortListForRws;
import com.refdata.grpc.PortServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

/**
 * gRPC-сервис выдачи справочника портов.
 * <p>
 * Поддерживает два endpoint с разными DTO-контрактами:
 * </p>
 * <ul>
 *   <li>{@code getAllPortsForRws} — расширенная проекция для {@code rws-api},</li>
 *   <li>{@code getAllPortsForRoute} — компактная проекция для {@code route-api}.</li>
 * </ul>
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PortGrpcService extends PortServiceGrpc.PortServiceImplBase {

    private final PortService portService;

    /**
     * Возвращает все порты в проекции для {@code rws-api}.
     *
     * @param request пустой запрос.
     * @param responseObserver observer для отправки gRPC-ответа.
     */
    @Override
    public void getAllPortsForRws(Empty request, StreamObserver<PortListForRws> responseObserver) {
        log.info("Request on get ports for RWS API");
        List<PortForRws> ports = portService.getAllLocksForRws();
        log.info("Ports for response RWS API: {}", ports.size());

        PortListForRws response = PortListForRws.newBuilder()
                .addAllPorts(
                        ports.stream()
                                .map(this::toPortRwsGrpc)
                                .toList()
                )
                .build();

        log.info("Send ports response on RWS API");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Маппинг доменного DTO порта в gRPC DTO для {@code rws-api}.
     */
    private com.refdata.grpc.PortForRws toPortRwsGrpc(PortForRws port) {
        return com.refdata.grpc.PortForRws.newBuilder()
                .setId(port.getId())
                .setName(port.getName())
                .setLatitude(port.getLatitude())
                .setLongitude(port.getLongitude())
                .build();
    }

    /**
     * Возвращает все порты в проекции для {@code route-api}.
     *
     * @param request пустой запрос.
     * @param responseObserver observer для отправки gRPC-ответа.
     */
    @Override
    public void getAllPortsForRoute(Empty request, StreamObserver<PortListForRoute> responseObserver) {
        log.info("Request on get ports for ROUTE API");
        List<PortForRoute> ports = portService.getAllPortsForRoute();
        log.info("Ports for response ROUTE API: {}", ports.size());

        PortListForRoute response = PortListForRoute.newBuilder()
                .addAllPorts(
                        ports.stream()
                                .map(this::toPortRouteGrpc)
                                .toList()
                )
                .build();

        log.info("Send ports response on ROUTE API");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Маппинг доменного DTO порта в компактный gRPC DTO для {@code route-api}.
     */
    private com.refdata.grpc.PortForRoute toPortRouteGrpc(PortForRoute port) {
        return com.refdata.grpc.PortForRoute.newBuilder()
                .setLatitude(port.getLatitude())
                .setLongitude(port.getLongitude())
                .build();
    }
}

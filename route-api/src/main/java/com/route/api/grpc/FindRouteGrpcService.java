package com.route.api.grpc;

import com.google.protobuf.Timestamp;
import com.route.api.business.core.client.RouteFinderRequest;
import com.route.api.business.core.enitites.RouteNode;
import com.route.api.business.core.exceptions.RouteNotFoundException;
import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.manager.FindRouteManager;
import com.route.api.rest.dto.RouteFinderResponse;
import com.route.grpc.RouteServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@GrpcService
@RequiredArgsConstructor
public class FindRouteGrpcService extends RouteServiceGrpc.RouteServiceImplBase {

    private final FindRouteManager findRouteManager;

    @Override
    public void findRoute(com.route.grpc.RouteFinderRequest request, StreamObserver<com.route.grpc.RouteFinderResponse> responseObserver) {
        try {
            // 1. Конвертация proto → domain
            RouteFinderRequest domainRequest =
                    new RouteFinderRequest(
                            request.getStartLongitude(),
                            request.getStartLatitude(),
                            request.getEndLongitude(),
                            request.getEndLatitude(),
                            toLocalDateTime(request.getDepartureTime()),
                            request.getSpeed()
                    );

            // 2. Вызов бизнес-логики
            RouteFinderResponse domainResponse = findRouteManager.findRoute(domainRequest);

            // 3. Конвертация domain → proto
            com.route.grpc.RouteFinderResponse protoResponse = mapToProto(domainResponse);

            responseObserver.onNext(protoResponse);
            responseObserver.onCompleted();

        } catch (RouteNotFoundException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Route not found")
                            .asRuntimeException()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal error")
                            .asRuntimeException()
            );
        }
    }


    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos(),
                ZoneOffset.UTC
        );
    }


    private com.route.grpc.RouteFinderResponse mapToProto(RouteFinderResponse response) {

        return com.route.grpc.RouteFinderResponse.newBuilder()
                .setDuration(response.duration())
                .setArrivalDateTime(toTimestamp(response.arrivalDateTime()))
                .setTotalDistance(response.totalDistance())
                .addAllRoute(
                        response.route().stream()
                                .map(this::mapNode)
                                .toList()
                )
                .addAllRouteLocks(
                        response.routeLocks().stream()
                                .map(this::mapLock)
                                .toList()
                )
                .build();
    }

    private com.route.grpc.RouteNode mapNode(RouteNode node) {
        return com.route.grpc.RouteNode.newBuilder()
                .setSeq(node.seq())
                .setNodeId(node.nodeId())
                .setLat(node.lat())
                .setLon(node.lon())
                .setCost(node.cost())
                .build();
    }

    private com.route.grpc.LockDto mapLock(LockDto lock) {
        return com.route.grpc.LockDto.newBuilder()
                .setId(lock.id())
                .setName(lock.name())
                .setLatitude(lock.latitude())
                .setLongitude(lock.longitude())
                .addAllNodeIds(lock.nodeIds())
                .build();
    }

    private Timestamp toTimestamp(LocalDateTime time) {
        return Timestamp.newBuilder()
                .setSeconds(time.toEpochSecond(ZoneOffset.UTC))
                .setNanos(time.getNano())
                .build();
    }
}

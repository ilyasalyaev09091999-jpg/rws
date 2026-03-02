package com.refdata.api.grpc;

import com.refdata.api.domain.api.LockService;
import com.refdata.api.domain.entities.route_api.LockForRoute;
import com.refdata.api.domain.entities.rws_api.LockForRws;
import com.refdata.grpc.Empty;
import com.refdata.grpc.LockListForRoute;
import com.refdata.grpc.LockListForRws;
import com.refdata.grpc.LockServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class LockGrpcService extends LockServiceGrpc.LockServiceImplBase {

    private final LockService service;

    @Override
    public void getAllLocksForRws(Empty request, StreamObserver<LockListForRws> responseObserver) {
        log.info("Request on get locks for RWS API");
        List<LockForRws> locks = service.getAllLocksForRws();
        log.info("Locks for response RWS API: {}", locks);

        LockListForRws response = LockListForRws.newBuilder()
                .addAllLocks(
                        locks.stream()
                                .map(this::toLockRwsGrpc)
                                .toList()
                )
                .build();

        log.info("Send locks response on RWS API");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private com.refdata.grpc.LockForRws toLockRwsGrpc(LockForRws lock) {
        return com.refdata.grpc.LockForRws.newBuilder()
                .setId(lock.getId())
                .setName(lock.getName())
                .setLatitude(lock.getLatitude())
                .setLongitude(lock.getLongitude())
                .build();
    }


    @Override
    public void getAllLocksForRoute(Empty request, StreamObserver<LockListForRoute> responseObserver) {
        log.info("Request on get locks for ROUTE API");
        List<LockForRoute> locks = service.getAllLocksForRoute();
        log.info("Locks for response ROUTE API: {}", locks);

        LockListForRoute response = LockListForRoute.newBuilder()
                .addAllLocks(
                        locks.stream()
                                .map(this::toLockRouteGrpc)
                                .toList()
                )
                .build();

        log.info("Send locks response on ROUTE API");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private com.refdata.grpc.LockForRoute toLockRouteGrpc(LockForRoute lock) {
        return com.refdata.grpc.LockForRoute.newBuilder()
                .setName(lock.getName())
                .addAllNodeIds(lock.getNodeIds())
                .build();
    }
}

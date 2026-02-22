package com.refdata.api.grpc;

import com.refdata.api.access_data.domain.api.LockService;
import com.refdata.api.access_data.domain.entities.Lock;
import com.refdata.grpc.Empty;
import com.refdata.grpc.LockList;
import com.refdata.grpc.RefDataServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class LockGrpcService extends RefDataServiceGrpc.RefDataServiceImplBase {

    private final LockService service;


    @Override
    public void getAllLocks(Empty request, StreamObserver<LockList> responseObserver) {
        List<Lock> locks = service.findAll();

        LockList response = LockList.newBuilder()
                .addAllLocks(
                        locks.stream()
                                .map(this::toLockDto)
                                .toList()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private com.refdata.grpc.Lock toLockDto(Lock lock) {
        return com.refdata.grpc.Lock.newBuilder()
                .setId(lock.getId())
                .setName(lock.getName())
                .build();
    }
}

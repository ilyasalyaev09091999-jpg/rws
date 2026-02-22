package com.refdata.api.grpc;

import com.refdata.api.access_data.domain.api.PortService;
import com.refdata.api.access_data.domain.entities.Port;
import com.refdata.grpc.Empty;
import com.refdata.grpc.PortList;
import com.refdata.grpc.RefDataServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class PortGrpcService extends RefDataServiceGrpc.RefDataServiceImplBase {

    private final PortService portService;

    @Override
    public void getAllPorts(Empty request, StreamObserver<PortList> responseObserver) {
        List<Port> ports = portService.findAll();

        PortList response = PortList.newBuilder()
                .addAllPorts(
                        ports.stream()
                                .map(this::toPortDto)
                                .toList()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private com.refdata.grpc.Port toPortDto(Port port) {
        return com.refdata.grpc.Port.newBuilder()
                .setId(port.getId())
                .setName(port.getName())
                .build();
    }
}

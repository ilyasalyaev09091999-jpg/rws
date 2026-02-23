package com.route.api.business.core.refdata.client;

import com.refdata.grpc.Empty;
import com.refdata.grpc.Lock;
import com.refdata.grpc.Port;
import com.refdata.grpc.RefDataServiceGrpc;
import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service("RefDataGrpcClient")
@RequiredArgsConstructor
public class RefDataGrpcClient implements RefDataClient {

    private final ModelMapper modelMapper;
    private final RefDataServiceGrpc.RefDataServiceBlockingStub stub;

    private volatile List<Lock> locks = List.of();
    private volatile List<Port> ports = List.of();

    @PostConstruct
    public void init() {
        refreshLocks();
        refreshPorts();
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refreshLocks() {
        try {
            locks = stub.getAllLocks(Empty.newBuilder().build()).getLocksList();
            log.info("Locks refreshed: {}", locks.size());
        } catch (Exception e) {
            log.warn("Failed to refresh locks: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public synchronized void refreshPorts() {
        try {
            ports = stub.getAllPorts(Empty.newBuilder().build()).getPortsList();
            log.info("Ports refreshed: {}", ports.size());
        } catch (Exception e) {
            log.warn("Failed to refresh ports: {}", e.getMessage());
        }
    }


    @Override
    public List<PortDto> getPorts() {
        return ports.stream().map(e -> modelMapper.map(e, PortDto.class)).toList();
    }

    @Override
    public List<LockDto> getLocks() {
        return locks.stream().map(e -> modelMapper.map(e, LockDto.class)).toList();
    }
}

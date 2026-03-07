package com.route.api.business.core.refdata.client;

import com.refdata.grpc.*;
import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * Бизнес-компонент route-api: RefDataGrpcClient.
 */
@Slf4j
@Service("RefDataGrpcClient")
@RequiredArgsConstructor
public class RefDataGrpcClient implements RefDataClient {

    @GrpcClient("refdata")
    private LockServiceGrpc.LockServiceBlockingStub stubLock;

    @GrpcClient("refdata")
    private PortServiceGrpc.PortServiceBlockingStub stubPort;

    private volatile List<LockForRoute> locks = List.of();
    private volatile List<PortForRoute> ports = List.of();

    /**
     * Выполняет первичную инициализацию данных после старта бина.
     */
    @PostConstruct
    public void init() {
        refreshLocks();
        refreshPorts();
    }

    /**
     * Обновляет локальный кэш данных, получаемых по gRPC.
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000) // РєР°Р¶РґС‹Рµ 10 РјРёРЅСѓС‚
    public synchronized void refreshLocks() {
        try {
            locks = stubLock.getAllLocksForRoute(Empty.newBuilder().build()).getLocksList();
            log.info("Locks refreshed: {}", locks.size());
        } catch (Exception e) {
            log.warn("Failed to refresh locks: {}", e.getMessage());
        }
    }

    /**
     * Обновляет локальный кэш данных, получаемых по gRPC.
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public synchronized void refreshPorts() {
        try {
            ports = stubPort.getAllPortsForRoute(Empty.newBuilder().build()).getPortsList();
            log.info("Ports refreshed: {}", ports.size());
        } catch (Exception e) {
            log.warn("Failed to refresh ports: {}", e.getMessage());
        }
    }


    /**
     * Возвращает список портов в формате доменных DTO.
     */
    @Override
    public List<PortDto> getPorts() {
        return ports.stream()
                .map(this::mapToPortDto).toList();
    }

    private PortDto mapToPortDto(PortForRoute port) {
        return new PortDto(port.getLatitude(), port.getLongitude());
    }


    /**
     * Возвращает список шлюзов в формате доменных DTO.
     */
    @Override
    public List<LockDto> getLocks() {
        return locks.stream()
                .map(this::mapToLockDto).toList();
    }

    private LockDto mapToLockDto(LockForRoute lock) {
        return new LockDto(lock.getName(), new HashSet<>(lock.getNodeIdsList()));
    }
}

package com.rws.api.cache;

import com.refdata.grpc.Empty;
import com.refdata.grpc.PortForRws;
import com.refdata.grpc.PortServiceGrpc;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortCacheService {

    @GrpcClient("refdata")
    private PortServiceGrpc.PortServiceBlockingStub stub;

    @Getter
    private volatile List<PortForRws> ports = List.of();

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refresh() {
        try {
            ports = stub.getAllPortsForRws(Empty.newBuilder().build()).getPortsList();
            log.info("Ports cache refreshed: ports = {}", ports.size());
        } catch (Exception e) {
            log.info("Failed to refresh ports cache: {}", e.getMessage());
        }
    }
}

package com.rws.api.cache;

import com.refdata.grpc.Empty;
import com.refdata.grpc.LockForRws;
import com.refdata.grpc.LockServiceGrpc;
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
public class LockCacheService {

    @GrpcClient("refdata")
    private LockServiceGrpc.LockServiceBlockingStub stub;

    @Getter
    private volatile List<LockForRws> locks = List.of();

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refresh() {
        try {
            locks = stub.getAllLocksForRws(Empty.newBuilder().build()).getLocksList();
            log.info("Locks cache refreshed: locks = {}", locks.size());
        } catch (Exception e) {
            log.info("Failed to refresh locks cache: {}", e.getMessage());
        }
    }

}

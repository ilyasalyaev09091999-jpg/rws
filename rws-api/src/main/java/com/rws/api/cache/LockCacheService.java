package com.rws.api.cache;

import com.refdata.grpc.Empty;
import com.refdata.grpc.Lock;
import com.refdata.grpc.RefDataServiceGrpc;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockCacheService {

    private final RefDataServiceGrpc.RefDataServiceBlockingStub stub;

    @Getter
    private volatile List<Lock> locks = List.of();

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refresh() {
        try {
            locks = stub.getAllLocks(Empty.newBuilder().build()).getLocksList();
            log.info("Locks cache refreshed: locks = {}", locks.size());
        } catch (Exception e) {
            log.info("Failed to refresh locks cache: {}", e.getMessage());
        }
    }

}

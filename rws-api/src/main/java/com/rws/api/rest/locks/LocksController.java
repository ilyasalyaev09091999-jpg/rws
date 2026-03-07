package com.rws.api.rest.locks;

import com.rws.api.cache.LockCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для выдачи справочной информации о шлюзах.
 * <p>
 * Контроллер использует {@link LockCacheService} как локальный источник данных,
 * который обновляется независимо от входящих HTTP-запросов.
 * </p>
 */
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/locks")
@RequiredArgsConstructor
public class LocksController {

    private final LockCacheService lockCacheService;

    /**
     * Возвращает список шлюзов в формате REST DTO.
     *
     * @return HTTP 200 c перечнем шлюзов из текущего снимка кэша.
     */
    @GetMapping("/get")
    public ResponseEntity<List<LockForRws>> getLocks() {
        log.info("Get locks request");
        List<com.refdata.grpc.LockForRws> grpcLockList = lockCacheService.getLocks();
        List<LockForRws> locks = grpcLockList.stream()
                .map(this::mapToDto).toList();
        log.info("Locks for response: {}", locks);
        return ResponseEntity.ok(locks);
    }

    private LockForRws mapToDto(com.refdata.grpc.LockForRws grpcLock) {
        LockForRws lock = new LockForRws();
        lock.setId(grpcLock.getId());
        lock.setName(grpcLock.getName());
        lock.setLatitude(grpcLock.getLatitude());
        lock.setLongitude(grpcLock.getLongitude());
        return lock;
    }
}

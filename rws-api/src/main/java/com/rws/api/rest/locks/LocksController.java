package com.rws.api.rest.locks;

import com.refdata.grpc.Lock;
import com.rws.api.cache.LockCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/locks")
@RequiredArgsConstructor
public class LocksController {

    private final LockCacheService lockCacheService;

    @GetMapping("/get")
    public ResponseEntity<List<Lock>> getLocks() {
        return ResponseEntity.ok(lockCacheService.getLocks());
    }
}

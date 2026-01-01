package com.refdata.api.rest;

import com.refdata.api.access_data.domain.api.LockService;
import com.refdata.api.access_data.domain.entities.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/locks")
public class LocksController {

    private final LockService lockService;

    @Autowired
    public LocksController(@Qualifier("LockJpaService") LockService lockService) {
        this.lockService = lockService;
    }


    @GetMapping("/get")
    public ResponseEntity<List<Lock>> getLocks() {
        return ResponseEntity.ok(lockService.findAll());
    }
}

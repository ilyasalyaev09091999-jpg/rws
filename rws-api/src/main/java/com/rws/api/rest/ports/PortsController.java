package com.rws.api.rest.ports;

import com.refdata.grpc.Port;
import com.rws.api.cache.PortCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ports")
@RequiredArgsConstructor
public class PortsController {

    private final PortCacheService portCacheService;

    @GetMapping("/get")
    public ResponseEntity<List<Port>> getPorts() {
        return ResponseEntity.ok(portCacheService.getPorts());
    }
}

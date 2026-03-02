package com.rws.api.rest.ports;

import com.rws.api.cache.PortCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ports")
@RequiredArgsConstructor
public class PortsController {

    private final PortCacheService portCacheService;

    @GetMapping("/get")
    public ResponseEntity<List<PortForRws>> getPorts() {
        log.info("Get ports request");
        List<com.refdata.grpc.PortForRws> grpcPorts = portCacheService.getPorts();
        List<PortForRws> ports = grpcPorts.stream()
                .map(this::mapToDto).toList();
        log.info("Ports for response: {}", ports);
        return ResponseEntity.ok(ports);
    }

    private PortForRws mapToDto(com.refdata.grpc.PortForRws grpcPort) {
        PortForRws port = new PortForRws();
        port.setId(grpcPort.getId());
        port.setName(grpcPort.getName());
        port.setLatitude(grpcPort.getLatitude());
        port.setLongitude(grpcPort.getLongitude());
        return port;
    }
}

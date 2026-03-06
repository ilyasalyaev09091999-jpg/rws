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

/**
 * REST-контроллер для выдачи справочной информации о портах.
 * <p>
 * Контроллер читает данные из локального кэша {@link PortCacheService},
 * который периодически синхронизируется с {@code refdata-api} по gRPC.
 * Такой подход снижает latency и исключает сетевой вызов на каждый запрос.
 * </p>
 */
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ports")
@RequiredArgsConstructor
public class PortsController {

    private final PortCacheService portCacheService;

    /**
     * Возвращает список портов в формате REST DTO.
     * <p>
     * Данные берутся из in-memory кэша и маппятся из gRPC-модели
     * {@code com.refdata.grpc.PortForRws} в локальную модель {@link PortForRws}.
     * </p>
     *
     * @return HTTP 200 c полным списком портов из актуального снимка кэша.
     */
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

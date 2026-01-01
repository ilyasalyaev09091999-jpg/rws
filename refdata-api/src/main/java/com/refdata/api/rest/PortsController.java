package com.refdata.api.rest;

import com.refdata.api.access_data.domain.api.PortService;
import com.refdata.api.access_data.domain.entities.Port;
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
@RequestMapping("/api/ports")
public class PortsController {

    private final PortService portService;

    @Autowired
    public PortsController(@Qualifier("PortJpaService") PortService portService) {
        this.portService = portService;
    }


    @GetMapping("/get")
    public ResponseEntity<List<Port>> getPorts() {
        return ResponseEntity.ok(portService.findAll());
    }
}

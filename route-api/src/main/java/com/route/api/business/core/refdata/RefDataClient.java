package com.route.api.business.core.refdata;

import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Component
public class RefDataClient {

    private final RestTemplate restTemplate;

    public RefDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LockDto> getAllLocks() {

        LockDto[] response = restTemplate.getForObject(
                "http://localhost:8092/api/locks/get",
                LockDto[].class
        );

        return response == null ? List.of() : Arrays.asList(response);
    }


    public List<PortDto> getAllPorts() {

        PortDto[] response = restTemplate.getForObject(
                "http://localhost:8092/api/ports/get",
                PortDto[].class
        );

        return response == null ? List.of() : Arrays.asList(response);
    }
}

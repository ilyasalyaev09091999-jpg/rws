package com.route.api.business.core.locks;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Component
public class LockClient {

    private final RestTemplate restTemplate;

    public LockClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LockDto> getAllLocks() {

        LockDto[] response = restTemplate.getForObject(
                "http://localhost:8092/api/locks/get",
                LockDto[].class
        );

        return response == null ? List.of() : Arrays.asList(response);
    }
}

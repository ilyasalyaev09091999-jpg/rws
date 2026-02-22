package com.route.api.business.core.refdata;

import com.route.api.business.core.refdata.locks.LockDto;
import com.route.api.business.core.refdata.ports.PortDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RefDataClient {

    private final RestTemplate restTemplate;

    public RefDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LockDto> getAllLocks() {
        LockDto[] response = null;
        try {
            response = restTemplate.getForObject(
                    "http://refdata-api:8092/api/locks/get",
                    LockDto[].class
            );
        } catch (Exception e) {
            log.warn("Not response from locks endpoint \n{}", Arrays.toString(e.getStackTrace()));
        }
        return response == null ? List.of() : Arrays.asList(response);
    }


    public List<PortDto> getAllPorts() {
        PortDto[] response = null;
        try {
            response = restTemplate.getForObject(
                    "http://refdata-api:8092/api/ports/get",
                    PortDto[].class
            );
        } catch (Exception e) {
            log.warn("Not response from ports endpoint \n{}", Arrays.toString(e.getStackTrace()));
        }
        return response == null ? List.of() : Arrays.asList(response);
    }
}

package com.rws.api.rest.archive.client;

import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class ArchiveApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${archive.api.base-url:http://localhost:8084}")
    private String archiveApiBaseUrl;

    public ArchiveImportResult importXlsx(MultipartFile file) throws RestClientException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl(archiveApiBaseUrl)
                .path("/api/archive/import/xlsx")
                .build()
                .toUriString();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", toResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, requestEntity, ArchiveImportResult.class);
    }

    public ArchiveTripSearchResponse search(String departurePoint,
                                            String destinationPoint,
                                            LocalDate dateFrom,
                                            LocalDate dateTo,
                                            int page,
                                            int size) throws RestClientException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(archiveApiBaseUrl)
                .path("/api/archive/search")
                .queryParam("page", page)
                .queryParam("size", size);

        addIfPresent(builder, "departurePoint", departurePoint);
        addIfPresent(builder, "destinationPoint", destinationPoint);
        addIfPresent(builder, "dateFrom", dateFrom);
        addIfPresent(builder, "dateTo", dateTo);

        return restTemplate.getForObject(builder.build().toUriString(), ArchiveTripSearchResponse.class);
    }

    public List<ArchiveRouteStatsItem> analytics(String departurePoint,
                                                 String destinationPoint,
                                                 Integer month) throws RestClientException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(archiveApiBaseUrl)
                .path("/api/archive/analytics");

        addIfPresent(builder, "departurePoint", departurePoint);
        addIfPresent(builder, "destinationPoint", destinationPoint);
        addIfPresent(builder, "month", month);

        ResponseEntity<List<ArchiveRouteStatsItem>> response = restTemplate.exchange(
                builder.build().toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    private ByteArrayResource toResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file", e);
        }
    }

    private void addIfPresent(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }
}

package com.rws.api.rest.archive;

import com.rws.api.rest.archive.client.ArchiveApiClient;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveApiClient archiveApiClient;

    @PostMapping(value = "/import/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchiveImportResult> importXlsx(@RequestPart("file") MultipartFile file) {
        log.info("Archive import request received");
        return ResponseEntity.ok(archiveApiClient.importXlsx(file));
    }

    @GetMapping("/search")
    public ResponseEntity<ArchiveTripSearchResponse> search(
            @RequestParam(required = false) String departurePoint,
            @RequestParam(required = false) String destinationPoint,
            @RequestParam(required = false) String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String effectiveDeparture = firstNonBlank(departurePoint, fromCity);
        String effectiveDestination = firstNonBlank(destinationPoint, toCity);
        log.info("Archive search request. departurePoint={}, destinationPoint={}, page={}, size={}", effectiveDeparture, effectiveDestination, page, size);
        return ResponseEntity.ok(archiveApiClient.search(effectiveDeparture, effectiveDestination, dateFrom, dateTo, page, size));
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<ArchiveRouteStatsItem>> analytics(
            @RequestParam(required = false) String departurePoint,
            @RequestParam(required = false) String destinationPoint,
            @RequestParam(required = false) String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(required = false) Integer month) {
        String effectiveDeparture = firstNonBlank(departurePoint, fromCity);
        String effectiveDestination = firstNonBlank(destinationPoint, toCity);
        log.info("Archive analytics request. departurePoint={}, destinationPoint={}, month={}", effectiveDeparture, effectiveDestination, month);
        return ResponseEntity.ok(archiveApiClient.analytics(effectiveDeparture, effectiveDestination, month));
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}

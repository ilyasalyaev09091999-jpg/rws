package com.archive.api.rest.archive;

import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.api.business.importer.ArchiveXlsxImportService;
import com.archive.api.business.read.ArchiveReadService;
import com.archive.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.archive.api.rest.archive.dto.ArchiveTripSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveImportController {

    private final ArchiveXlsxImportService archiveXlsxImportService;
    private final ArchiveReadService archiveReadService;

    @PostMapping(value = "/import/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchiveImportResult> importXlsx(@RequestPart("file") MultipartFile file) {
        ArchiveImportResult result = archiveXlsxImportService.importFile(file);
        return ResponseEntity.ok(result);
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
        return ResponseEntity.ok(archiveReadService.search(effectiveDeparture, effectiveDestination, dateFrom, dateTo, page, size));
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
        return ResponseEntity.ok(archiveReadService.stats(effectiveDeparture, effectiveDestination, month));
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

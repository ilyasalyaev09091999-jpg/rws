package com.archive.api.rest.archive;

import com.archive.api.business.importer.ArchiveImportResult;
import com.archive.api.business.importer.ArchiveXlsxImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveImportController {

    private final ArchiveXlsxImportService archiveXlsxImportService;

    @PostMapping(value = "/import/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchiveImportResult> importXlsx(@RequestPart("file") MultipartFile file) {
        ArchiveImportResult result = archiveXlsxImportService.importFile(file);
        return ResponseEntity.ok(result);
    }
}

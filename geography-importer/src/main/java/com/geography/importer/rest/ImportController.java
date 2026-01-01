package com.geography.importer.rest;

import com.geography.importer.business.importpbf.manager.ImportPbfManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportPbfManager importPbfManager;

    @PostMapping
    public void importPbf() throws IOException {
        importPbfManager.execute();
    }
}

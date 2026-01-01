package com.geography.importer.business.importpbf.manager;

import com.geography.importer.business.importpbf.core.filetodb.OsmImportService;
import com.geography.importer.business.importpbf.core.grafbuilder.GraphBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImportPbfManager {

    private final OsmImportService importService;
    private final GraphBuilderService graphBuilderService;

    private static boolean isProcessed = false;

    @Async
    public void execute() throws IOException {
        try {
            if (isProcessed) {
                return;
            }

            isProcessed = true;

            importService.importWaterways();
            graphBuilderService.buildEdges();
        } finally {
            isProcessed = false;
        }
    }
}

package com.geography.importer.business.importpbf.manager;

import com.geography.importer.business.importpbf.core.filetodb.OsmImportService;
import com.geography.importer.business.importpbf.core.grafbuilder.GraphBuilderService;
import com.geography.importer.business.importpbf.core.grafbuilder.GraphMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Оркестратор пайплайна импорта геоданных.
 * <p>
 * Последовательно выполняет:
 * </p>
 * <ul>
 *   <li>импорт OSM сущностей водных путей в таблицы {@code nodes/ways/way_nodes};</li>
 *   <li>построение таблицы рёбер графа и обновление materialized view.</li>
 * </ul>
 * <p>
 * Защищает от параллельного повторного запуска с помощью флага
 * {@code isProcessed}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ImportPbfManager {

    private final OsmImportService importService;
    private final GraphBuilderService graphBuilderService;
    private final GraphMetadataService graphMetadataService;

    private static boolean isProcessed = false;

    /**
     * Асинхронно запускает полный цикл импорта и построения графа.
     * <p>
     * Если процесс уже выполняется, метод завершится без повторного запуска.
     * </p>
     *
     * @throws IOException если возникла ошибка при чтении ресурсов или SQL-скрипта.
     */
    @Async
    public void execute() throws IOException {
        try {
            if (isProcessed) {
                return;
            }

            isProcessed = true;

            importService.importWaterways();
            graphBuilderService.buildEdges();
            graphMetadataService.rebuildMetadata();
        } finally {
            isProcessed = false;
        }
    }

    /**
     * Асинхронно пересчитывает только производные метаданные графа без повторного импорта регионов.
     *
     * @throws IOException если возникла ошибка чтения SQL-ресурса.
     */
    @Async
    public void rebuildGraphMetadata() throws IOException {
        try {
            if (isProcessed) {
                return;
            }

            isProcessed = true;
            graphMetadataService.rebuildMetadata();
        } finally {
            isProcessed = false;
        }
    }
}

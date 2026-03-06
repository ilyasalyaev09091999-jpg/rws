package com.geography.importer.rest;

import com.geography.importer.business.importpbf.manager.ImportPbfManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST-контроллер запуска импорта OSM PBF в базу {@code geography}.
 * <p>
 * Предоставляет технический endpoint для ручного или внешнего триггера
 * процесса загрузки данных и построения рёбер графа.
 * </p>
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportPbfManager importPbfManager;

    /**
     * Запускает асинхронный пайплайн импорта геоданных.
     *
     * @throws IOException если на этапе обработки файла или построения графа
     *                     произошла ошибка ввода-вывода.
     */
    @PostMapping
    public void importPbf() throws IOException {
        importPbfManager.execute();
    }
}

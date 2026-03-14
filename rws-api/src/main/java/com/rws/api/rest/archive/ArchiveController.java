package com.rws.api.rest.archive;

import com.rws.api.rest.archive.client.ArchiveApiClient;
import com.rws.api.rest.archive.dto.ArchiveImportJobStatus;
import com.rws.api.rest.archive.dto.ArchiveImportResult;
import com.rws.api.rest.archive.dto.ArchiveRouteStatsItem;
import com.rws.api.rest.archive.dto.ArchiveTripSearchResponse;
import com.rws.api.rest.archive.dto.filter.ArchiveAnalyticsFilter;
import com.rws.api.rest.archive.dto.filter.ArchiveSearchFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST API для работы с архивом рейсов в {@code rws-api}.
 *
 * <p>Контроллер выступает фасадом между UI и {@code archive-api}:
 * принимает HTTP-запросы, валидирует входные данные и делегирует
 * операции в {@link ArchiveApiClient}, который общается с {@code archive-api} по gRPC.</p>
 */
@Slf4j
@Validated
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveApiClient archiveApiClient;

    /**
     * Синхронный импорт XLSX.
     *
     * <p>Запрос блокируется до завершения обработки файла.</p>
     *
     * @param file XLSX-файл из multipart запроса
     * @return статистика импорта по файлу
     */
    @PostMapping(value = "/import/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchiveImportResult> importXlsx(@RequestPart("file") MultipartFile file) {
        log.info("Archive sync import request received");
        return ResponseEntity.ok(archiveApiClient.importXlsx(file));
    }

    /**
     * Асинхронный импорт XLSX.
     *
     * @param file XLSX-файл из multipart запроса
     * @return начальный статус задачи с {@code jobId}
     */
    @PostMapping(value = "/import/xlsx/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchiveImportJobStatus> startImportXlsxAsync(@RequestPart("file") MultipartFile file) {
        log.info("Archive async import request received");
        return ResponseEntity.ok(archiveApiClient.startImportXlsx(file));
    }

    /**
     * Получение статуса асинхронного импорта по {@code jobId}.
     *
     * @param jobId идентификатор задачи, полученный из {@link #startImportXlsxAsync(MultipartFile)}
     * @return актуальный статус задачи с счетчиками и возможной ошибкой
     */
    @GetMapping("/import/jobs/{jobId}")
    public ResponseEntity<ArchiveImportJobStatus> getImportStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(archiveApiClient.getImportJobStatus(jobId));
    }

    /**
     * Поиск архивных рейсов по фильтрам.
     *
     * @param filter валидированный набор фильтров (поддерживаются legacy-алиасы)
     * @return результат поиска с элементами и пагинацией
     */
    @GetMapping("/search")
    public ResponseEntity<ArchiveTripSearchResponse> search(@Valid @ModelAttribute ArchiveSearchFilter filter) {
        String departure = filter.effectiveDeparturePoint();
        String destination = filter.effectiveDestinationPoint();
        int page = filter.getPage() == null ? 0 : filter.getPage();
        int size = filter.getSize() == null ? 20 : filter.getSize();

        log.info("Archive search request. departurePoint={}, destinationPoint={}, page={}, size={}", departure, destination, page, size);

        return ResponseEntity.ok(
                archiveApiClient.search(
                        departure,
                        destination,
                        filter.getDateFrom(),
                        filter.getDateTo(),
                        page,
                        size
                )
        );
    }

    /**
     * Получение агрегированной статистики по маршрутам архива.
     *
     * @param filter валидированный набор фильтров статистики
     * @return список статистических элементов
     */
    @GetMapping("/analytics")
    public ResponseEntity<List<ArchiveRouteStatsItem>> analytics(@Valid @ModelAttribute ArchiveAnalyticsFilter filter) {
        String departure = filter.effectiveDeparturePoint();
        String destination = filter.effectiveDestinationPoint();

        log.info("Archive analytics request. departurePoint={}, destinationPoint={}, month={}", departure, destination, filter.getMonth());

        return ResponseEntity.ok(archiveApiClient.analytics(departure, destination, filter.getMonth()));
    }
}
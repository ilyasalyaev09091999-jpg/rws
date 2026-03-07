package com.archive.api.business.importer;

import com.archive.api.access_data.db.jpa.model.ArchiveTripEntity;
import com.archive.api.access_data.db.jpa.repository.ArchiveTripJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveXlsxImportService {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("d.M.uuuu"),
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            DateTimeFormatter.ofPattern("d/M/uuuu"),
            DateTimeFormatter.ofPattern("dd/MM/uuuu"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    private static final String RU_REIS = "\u0440\u0435\u0439\u0441";
    private static final String RU_BUKSIR = "\u0431\u0443\u043a\u0441\u0438\u0440";
    private static final String RU_REGION = "\u0440\u0435\u0433\u0438\u043e\u043d";
    private static final String RU_KOLICHESTVO = "\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432";
    private static final String RU_GRUZ = "\u0433\u0440\u0443\u0437";
    private static final String RU_OSADK = "\u043e\u0441\u0430\u0434\u043a";
    private static final String RU_KONTRAGENT = "\u043a\u043e\u043d\u0442\u0440\u0430\u0433\u0435\u043d\u0442";
    private static final String RU_INN = "\u0438\u043d\u043d";
    private static final String RU_FLAG = "\u0444\u043b\u0430\u0433";
    private static final String RU_PUNKT = "\u043f\u0443\u043d\u043a\u0442";
    private static final String RU_GOROD = "\u0433\u043e\u0440\u043e\u0434";
    private static final String RU_OTKUDA = "\u043e\u0442\u043a\u0443\u0434\u0430";
    private static final String RU_OTPRAV = "\u043e\u0442\u043f\u0440\u0430\u0432";
    private static final String RU_OTPR = "\u043e\u0442\u043f\u0440";
    private static final String RU_KUDA = "\u043a\u0443\u0434\u0430";
    private static final String RU_PRIB = "\u043f\u0440\u0438\u0431";
    private static final String RU_NAZNACH = "\u043d\u0430\u0437\u043d\u0430\u0447";
    private static final String RU_DATA = "\u0434\u0430\u0442\u0430";
    private static final String RU_TIP = "\u0442\u0438\u043f";

    private final ArchiveTripJpaRepository tripRepository;
    private final PlatformTransactionManager transactionManager;

    public ArchiveImportResult importFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown.xlsx");

        int totalRows = 0;
        int importedRows = 0;
        int skippedRows = 0;
        int errorRows = 0;

        TransactionTemplate rowTx = new TransactionTemplate(transactionManager);
        rowTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                return new ArchiveImportResult(fileName, 0, 0, 0, 0);
            }

            HeaderDetectionResult detection = detectColumns(sheet.getRow(sheet.getFirstRowNum()));
            Map<String, Integer> columns = detection.columns();
            validateRequiredColumns(columns, detection.rawHeaders());

            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowBlank(row)) {
                    continue;
                }

                totalRows++;
                int sourceRowNum = i + 1;

                try {
                    ImportRowStatus status = rowTx.execute(txStatus -> importSingleRow(fileName, sourceRowNum, row, columns));
                    if (status == ImportRowStatus.IMPORTED) {
                        importedRows++;
                    } else {
                        skippedRows++;
                    }
                } catch (Exception ex) {
                    errorRows++;
                    log.warn("Archive import row failed. file={}, row={}, reason={}", fileName, sourceRowNum, ex.getMessage());
                }
            }

            return new ArchiveImportResult(fileName, totalRows, importedRows, skippedRows, errorRows);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read XLSX file", e);
        }
    }

    private ImportRowStatus importSingleRow(String fileName, int sourceRowNum, Row row, Map<String, Integer> columns) {
        if (tripRepository.existsBySourceFileNameAndSourceRowNum(fileName, sourceRowNum)) {
            return ImportRowStatus.SKIPPED;
        }

        ArchiveTripEntity entity = mapRow(fileName, sourceRowNum, row, columns);
        try {
            tripRepository.saveAndFlush(entity);
            return ImportRowStatus.IMPORTED;
        } catch (DataIntegrityViolationException ex) {
            return ImportRowStatus.SKIPPED;
        }
    }

    private ArchiveTripEntity mapRow(String fileName, int sourceRowNum, Row row, Map<String, Integer> columns) {
        String departurePoint = readString(row, columns.get("departure_point"));
        String destinationPoint = readString(row, columns.get("destination_point"));
        LocalDate departureDate = readDate(row, columns.get("departure_date"));
        LocalDate arrivalDate = readDate(row, columns.get("arrival_date"));

        if (isBlank(departurePoint) || isBlank(destinationPoint) || departureDate == null || arrivalDate == null) {
            throw new IllegalArgumentException("Required values are missing in row");
        }

        ArchiveTripEntity entity = new ArchiveTripEntity();
        entity.setSourceFileName(fileName);
        entity.setSourceRowNum(sourceRowNum);
        entity.setSourceSystem("xlsx");

        entity.setVoyageName(readString(row, columns.get("voyage_name")));
        entity.setTripType(readString(row, columns.get("trip_type")));
        entity.setTugName(readString(row, columns.get("tug_name")));

        entity.setDeparturePoint(departurePoint);
        entity.setDestinationPoint(destinationPoint);
        entity.setDepartureDate(departureDate);
        entity.setArrivalDate(arrivalDate);

        entity.setCargoType(readString(row, columns.get("cargo_type")));
        entity.setCargoAmount(readDecimal(row, columns.get("cargo_amount")));
        entity.setUnitsCount(readInteger(row, columns.get("units_count")));
        entity.setDraftM(readDecimal(row, columns.get("draft_m")));

        entity.setCounterpartyName(readString(row, columns.get("counterparty_name")));
        entity.setCounterpartyInn(readString(row, columns.get("counterparty_inn")));
        entity.setFlag(readString(row, columns.get("flag")));

        entity.setRegionFrom(readString(row, columns.get("region_from")));
        entity.setRegionTo(readString(row, columns.get("region_to")));

        return entity;
    }

    private void validateRequiredColumns(Map<String, Integer> columns, List<String> rawHeaders) {
        List<String> required = List.of("departure_point", "destination_point", "departure_date", "arrival_date");
        List<String> missing = required.stream().filter(key -> !columns.containsKey(key)).toList();

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing required columns: " + String.join(", ", missing) +
                            ". Parsed headers: " + String.join(" | ", rawHeaders)
            );
        }
    }

    private HeaderDetectionResult detectColumns(Row header) {
        if (header == null) {
            throw new IllegalArgumentException("Header row is missing");
        }

        Map<String, Integer> map = new HashMap<>();
        List<String> rawHeaders = new ArrayList<>();

        short last = header.getLastCellNum();
        for (int i = 0; i < last; i++) {
            String rawHeader = readString(header, i);
            if (isBlank(rawHeader)) {
                continue;
            }

            rawHeaders.add(rawHeader);
            String normalized = normalize(rawHeader);

            if (isDepartureCityHeader(normalized)) {
                map.putIfAbsent("departure_point", i);
            } else if (isArrivalCityHeader(normalized)) {
                map.putIfAbsent("destination_point", i);
            } else if (isDepartureDateHeader(normalized)) {
                map.putIfAbsent("departure_date", i);
            } else if (isArrivalDateHeader(normalized)) {
                map.putIfAbsent("arrival_date", i);
            } else if (containsAny(normalized, RU_REIS, "voyage", "trip")) {
                map.putIfAbsent("voyage_name", i);
            } else if (isTripTypeHeader(normalized)) {
                map.putIfAbsent("trip_type", i);
            } else if (containsAny(normalized, RU_BUKSIR, "tug")) {
                map.putIfAbsent("tug_name", i);
            } else if (containsAny(normalized, RU_REGION + " " + RU_OTPRAV, "region from")) {
                map.putIfAbsent("region_from", i);
            } else if (containsAny(normalized, RU_REGION + " " + RU_PRIB, "region to", "region arrival")) {
                map.putIfAbsent("region_to", i);
            } else if (containsAny(normalized, RU_KOLICHESTVO + " " + RU_GRUZ, "cargo amount", "cargo qty", "cargo quantity")) {
                map.putIfAbsent("cargo_amount", i);
            } else if (containsAny(normalized, RU_OSADK, "draft")) {
                map.putIfAbsent("draft_m", i);
            } else if (containsAny(normalized, RU_KONTRAGENT)) {
                map.putIfAbsent("counterparty_name", i);
            } else if (containsAny(normalized, RU_INN, "inn")) {
                map.putIfAbsent("counterparty_inn", i);
            } else if (containsAny(normalized, RU_FLAG, "flag")) {
                map.putIfAbsent("flag", i);
            } else if (containsAny(normalized, RU_KOLICHESTVO, "qty", "quantity")
                    && !containsAny(normalized, RU_GRUZ, "cargo")) {
                map.putIfAbsent("units_count", i);
            } else if (containsAny(normalized, RU_GRUZ, "cargo")) {
                map.putIfAbsent("cargo_type", i);
            }
        }

        return new HeaderDetectionResult(map, rawHeaders);
    }

    private boolean isDepartureCityHeader(String normalized) {
        return (containsAny(normalized, RU_PUNKT, RU_GOROD, "city", "port", RU_OTKUDA, "from", "origin")
                && containsAny(normalized, RU_OTPRAV, RU_OTPR, "from", "origin"))
                || containsAny(normalized, RU_PUNKT + " " + RU_OTPRAV, RU_GOROD + " " + RU_OTPRAV);
    }

    private boolean isArrivalCityHeader(String normalized) {
        return (containsAny(normalized, RU_PUNKT, RU_GOROD, "city", "port", RU_KUDA, "to", "dest", "destination")
                && containsAny(normalized, RU_PRIB, RU_NAZNACH, "to", "dest", "destination"))
                || containsAny(normalized, RU_PUNKT + " " + RU_NAZNACH, RU_GOROD + " " + RU_NAZNACH, RU_PUNKT + " " + RU_PRIB, RU_GOROD + " " + RU_PRIB);
    }

    private boolean isDepartureDateHeader(String normalized) {
        return containsAny(normalized, RU_DATA)
                && containsAny(normalized, RU_OTPRAV, RU_OTPR, "departure", "dep", "start");
    }

    private boolean isArrivalDateHeader(String normalized) {
        return containsAny(normalized, RU_DATA)
                && containsAny(normalized, RU_PRIB, "arrival", "arrive", "end");
    }

    private boolean isTripTypeHeader(String normalized) {
        return containsAny(normalized, RU_TIP, "type") && !containsAny(normalized, RU_GRUZ, "cargo");
    }

    private String readString(Row row, Integer colIndex) {
        if (colIndex == null) {
            return null;
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> trimToNull(cell.getStringCellValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield DateTimeFormatter.ISO_LOCAL_DATE.format(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                double value = cell.getNumericCellValue();
                if (Math.floor(value) == value) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield trimToNull(cell.getStringCellValue());
                } catch (IllegalStateException ex) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    private LocalDate readDate(Row row, Integer colIndex) {
        if (colIndex == null) {
            return null;
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String value = readString(row, colIndex);
        if (isBlank(value)) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Invalid date format: " + value);
    }

    private BigDecimal readDecimal(Row row, Integer colIndex) {
        if (colIndex == null) {
            return null;
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String value = readString(row, colIndex);
        if (isBlank(value)) {
            return null;
        }

        String normalized = value.replace(" ", "").replace(",", ".");
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer readInteger(Row row, Integer colIndex) {
        BigDecimal decimal = readDecimal(row, colIndex);
        if (decimal == null) {
            return null;
        }
        try {
            return decimal.intValueExact();
        } catch (ArithmeticException ex) {
            return decimal.intValue();
        }
    }

    private boolean isRowBlank(Row row) {
        short firstCell = row.getFirstCellNum();
        short lastCell = row.getLastCellNum();

        if (firstCell < 0 || lastCell < 0) {
            return true;
        }

        for (int i = firstCell; i < lastCell; i++) {
            if (!isBlank(readString(row, i))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAny(String value, String... candidates) {
        for (String candidate : candidates) {
            if (value.contains(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT)
                .replace('\u0451', '\u0435')
                .replaceAll("[\\p{Punct}]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String result = value.trim();
        return result.isEmpty() ? null : result;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record HeaderDetectionResult(Map<String, Integer> columns, List<String> rawHeaders) {
    }

    private enum ImportRowStatus {
        IMPORTED,
        SKIPPED
    }
}



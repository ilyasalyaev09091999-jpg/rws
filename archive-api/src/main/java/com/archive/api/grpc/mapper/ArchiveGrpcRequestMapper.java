package com.archive.api.grpc.mapper;

import com.archive.api.util.DateParserUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ArchiveGrpcRequestMapper {

    public String normalizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "unknown.xlsx";
        }
        return fileName;
    }

    public String nullIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    public LocalDate parseDate(String value) {
        return DateParserUtils.parseIsoDateOrNull(value);
    }

    public Integer monthOrNull(int month) {
        return month > 0 ? month : null;
    }
}

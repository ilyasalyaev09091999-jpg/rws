package com.archive.api.business.read.dto;

import java.util.List;

public record ArchiveTripSearchResponse(
        List<ArchiveTripSearchItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}

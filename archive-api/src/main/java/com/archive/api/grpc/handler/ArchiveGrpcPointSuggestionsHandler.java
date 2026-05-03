package com.archive.api.grpc.handler;

import com.archive.api.business.read.ArchivePointSuggestionsService;
import com.archive.grpc.ArchivePointSuggestionsRequest;
import com.archive.grpc.ArchivePointSuggestionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handles gRPC requests for archive point suggestions.
 */
@Component
@RequiredArgsConstructor
public class ArchiveGrpcPointSuggestionsHandler {

    private final ArchivePointSuggestionsService archivePointSuggestionsService;

    public ArchivePointSuggestionsResponse handle(ArchivePointSuggestionsRequest request) {
        return ArchivePointSuggestionsResponse.newBuilder()
                .addAllPoints(archivePointSuggestionsService.getPoints())
                .build();
    }
}

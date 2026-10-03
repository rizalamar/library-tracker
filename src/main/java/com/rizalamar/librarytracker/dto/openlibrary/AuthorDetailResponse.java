package com.rizalamar.librarytracker.dto.openlibrary;

import lombok.Builder;
import java.util.List;

@Builder
public record AuthorDetailResponse(
        String name,
        String personalName,
        String fullerName,
        String birthDate,
        String bio,
        List<String> photos,
        List<OpenLibraryAuthorResponse.Link> links,
        List<String> alternateNames,
        List<AuthorWork> topWorks
) {
    @Builder
    public record AuthorWork(
            String key,
            String title,
            String firstPublishYear,
            String coverUrl
    ) {}
}

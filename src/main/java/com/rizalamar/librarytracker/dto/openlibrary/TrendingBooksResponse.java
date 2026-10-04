package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrendingBooksResponse(
        String query,
        List<Work> works
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Work(
            String key,
            String title,
            List<String> author_key,
            List<String> author_name,
            Integer cover_i,
            String firstPublishYear
    ) {}
}

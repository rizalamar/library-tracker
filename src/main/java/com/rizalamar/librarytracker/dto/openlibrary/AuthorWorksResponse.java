package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorWorksResponse(
        int size,
        List<WorkEntry> entries
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WorkEntry(
            String key,
            String title,
            @JsonProperty("first_publish_year") Object firstPublishYear,
            List<Integer> covers
    ) {
        public String getPublishYearString() {
            return firstPublishYear != null ? String.valueOf(firstPublishYear) : null;
        }
    }
}

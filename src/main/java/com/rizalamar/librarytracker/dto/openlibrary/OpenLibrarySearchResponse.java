package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibrarySearchResponse(
        Integer numFound,
        Integer start,
        List<Document> docs
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(
            String key,
            String title,
            List<String> author_name,
            List<String> author_key,
            Integer cover_i,
            String cover_edition_key,
            List<String> edition_key,
            List<String> isbn,
            String ebook_access,
            JsonNode availability
    ){}
}

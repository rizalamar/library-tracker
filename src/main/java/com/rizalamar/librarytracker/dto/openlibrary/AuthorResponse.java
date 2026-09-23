package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorResponse(
        String key,
        String name
) {
}

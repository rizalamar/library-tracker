package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryAuthorResponse(
        String name,
        @JsonProperty("personal_name") String personalName,
        @JsonProperty("fuller_name") String fullerName,
        @JsonProperty("birth_date") String birthDate,
        JsonNode bio,
        List<Integer> photos,
        List<Link> links,
        @JsonProperty("alternate_names") List<String> alternateNames
) {
    public record Link(String url, String title) {}
}

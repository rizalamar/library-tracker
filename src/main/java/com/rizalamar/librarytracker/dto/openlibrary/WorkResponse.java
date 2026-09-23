package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkResponse(
        String title,
        List<String> subjects,
        List<String> subject_places,
        List<String> subject_people,
        List<String> subject_times
) {
}

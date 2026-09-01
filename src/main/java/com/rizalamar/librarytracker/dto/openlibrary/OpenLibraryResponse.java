package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryResponse(
        String title,
        List<Author> authors,
        List<Publisher> publishers,
        Integer number_of_pages,
        List<String> subjects,
        List<String> subject_places,
        List<String> subject_people,
        List<String> subject_times,
        List<String> excerpts,
        String publish_date,
        Cover cover,
        String subtitle
) {
    public record Author(String url, String name){}
    public record Cover(String large, String medium, String small){}
    public record Publisher(String name) {}
}

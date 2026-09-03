package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryResponse(
        String title,
        List<Author> authors,
        List<Publisher> publishers,
        Integer number_of_pages,
        List<Subject> subjects,
        List<SubjectPlace> subject_places,
        List<SubjectPeople> subject_people,
        List<SubjectTimes> subject_times,
        List<Excerpt> excerpts,
        String publish_date,
        Cover cover,
        String subtitle
) {
    public record Author(String url, String name){}
    public record Cover(String large, String medium, String small){}
    public record Publisher(String name) {}
    public record Subject(String name){}
    public record SubjectPlace(String name){}
    public record SubjectPeople(String name){}
    public record SubjectTimes(String name){}
    public record Excerpt(String text, String comment){}
}

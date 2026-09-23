package com.rizalamar.librarytracker.dto.book;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record BookResponse(
        UUID id,
        String title,
        String isbn,
        String description,
        List<Author> authors,
        List<String> publishers,
        Integer number_of_pages,
        String physicalFormat,
        List<String> languages,
        List<String> publishPlaces,
        List<String> subjects,
        List<String> subjectsPeople,
        List<String> subjectPlaces,
        List<String> subjectTimes,
        String publishedDate,
        String imageUrl,
        boolean available,
        LocalDateTime createdAt
) {
    public record Author(String name, String url) {
    }

}

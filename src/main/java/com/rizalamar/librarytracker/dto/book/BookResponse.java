package com.rizalamar.librarytracker.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record BookResponse(
        UUID id,
        String title,
        String isbn,
        String subtitle,
        List<Author> authors,
        List<Publishers> publishers,
        Integer number_of_pages,
        List<String> subjects,
        List<String> subjectsPeople,
        List<String> subjectPlaces,
        List<String> subjectTimes,
        List<Excerpts> excerpts,
        String publishedDate,
        String imageUrl,
        boolean available,
        LocalDateTime createdAt
) {
    public record Author(String url, String name){}
    public record Publishers(String name){}
    public record Excerpts(String text, String comment){}
}

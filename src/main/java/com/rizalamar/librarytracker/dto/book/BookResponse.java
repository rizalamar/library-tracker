package com.rizalamar.librarytracker.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record BookResponse(
        UUID id,
        String title,
        List<Author> authors,
        String isbn,
        String subtitle,
        List<Publishers> publishers,
        String publishedDate,
        String imageUrl,
        boolean available,
        LocalDateTime createdAt
) {
    public record Author(String url, String name){}
    public record Publishers(String name){}
}

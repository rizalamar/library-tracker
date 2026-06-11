package com.rizalamar.librarytracker.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookResponse(
        UUID id,
        String title,
        String author,
        String isbn,
        String description,
        String publisher,
        String publishedDate,
        String imageUrl,
        boolean available,
        LocalDateTime createdAt
) {
}

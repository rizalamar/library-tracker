package com.rizalamar.librarytracker.dto.book;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        String author,

        String isbn,
        String description,
        String publisher,
        String publishedDate,
        String imageUrl
) {
}

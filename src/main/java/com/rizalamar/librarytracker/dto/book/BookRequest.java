package com.rizalamar.librarytracker.dto.book;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        List<Author> authors,

        String isbn,
        String subtitle,
        List<Publisher> publishers,
        String publishedDate,
        String imageUrl
) {
        public record Author(String url, String name){}
        public record Publisher(String name){}
}

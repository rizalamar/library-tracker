package com.rizalamar.librarytracker.dto.mybook;

import com.rizalamar.librarytracker.dto.book.BookRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MyBookRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotEmpty(message = "Author is required")
        List<BookRequest.Author> authors,

        @NotBlank(message = "Isbn is Required")
        String isbn,

        String subtitle,

        @NotEmpty(message = "Publisher us required")
        List<BookRequest.Publisher> publishers,
        String publishedDate,
        String imageUrl
) {
    public record Author(String url, String name){}
    public record Publisher(String name){}
}

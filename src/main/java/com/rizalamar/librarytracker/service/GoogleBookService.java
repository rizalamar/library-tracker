package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.googlebooks.GoogleBooksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GoogleBookService {
    private final RestTemplate restTemplate;
    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public BookResponse fetchBookByIsbn(String isbn){
        String url = GOOGLE_BOOKS_API + isbn;
        GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

        if(response == null || response.items() == null || response.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book found");
        }

        GoogleBooksResponse.VolumeInfo volumeInfo = response.items().getFirst().volumeInfo();

        return BookResponse.builder()
                .title(volumeInfo.title())
                .author(volumeInfo.authors() != null ? String.join(", ", volumeInfo.authors()) : "Unknown")
                .isbn(isbn)
                .description(volumeInfo.description())
                .publisher(volumeInfo.publisher())
                .publishedDate(volumeInfo.publishedDate())
                .imageUrl(volumeInfo.imageLinks() != null ? volumeInfo.imageLinks().thumbnail() : null)
                .available(true)
                .build();
    }
}

package com.rizalamar.librarytracker.dto.openlibrary;


import java.util.List;

public record ReadableBookSearchResponse(
        String query,
        int page,
        int limit,
        int candidateTotal,
        boolean hasNextCandidatePage,
        List<Book> books
) {
    public record Book(
            String editionKey,
            String isbn,
            String title,
            List<String> authors,
            String imageUrl,
            boolean readOnline,
            String readerUrl
    ){}
}

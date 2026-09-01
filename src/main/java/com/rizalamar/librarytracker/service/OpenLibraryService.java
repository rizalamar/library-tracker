package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibraryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String OPEN_LIBRARY_API = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&format=json&jscmd=data";

    public BookResponse fetchBookByIsbn(String isbn){
        String url = String.format(OPEN_LIBRARY_API, isbn);

        Map<String, Object> responseMap = restTemplate.getForObject(url, Map.class);

        if (responseMap == null || responseMap.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in Open Library");
        }

        Object data = responseMap.get("ISBN:" + isbn);
        OpenLibraryResponse openLibraryResponse = objectMapper.convertValue(data, OpenLibraryResponse.class);

        return BookResponse.builder()
                .title(openLibraryResponse.title())
                .authors(
                        openLibraryResponse.authors() != null ?
                                openLibraryResponse.authors().stream().map(author -> new BookResponse.Author(author.url(), author.name())).toList() : List.of()
                        )
                .isbn(isbn)
                .publishers(
                        openLibraryResponse.publishers() != null ?
                                openLibraryResponse.publishers().stream().map(publisher -> new BookResponse.Publishers(publisher.name())).toList() : List.of()
                        )
                .number_of_pages(openLibraryResponse.number_of_pages())
                .subjects(openLibraryResponse.subjects() != null ? openLibraryResponse.subjects() : List.of())
                .subjectPlaces(openLibraryResponse.subject_places() != null ? openLibraryResponse.subject_places() : List.of())
                .subjectsPeople(openLibraryResponse.subject_people() != null ? openLibraryResponse.subject_people() : List.of())
                .subjectTimes(openLibraryResponse.subject_times() != null ? openLibraryResponse.subject_times() : List.of())
                .excerpts(openLibraryResponse.excerpts() != null ? openLibraryResponse.excerpts() : List.of())
                .subtitle(openLibraryResponse.subtitle())
                .publishedDate(openLibraryResponse.publish_date())
                .imageUrl(openLibraryResponse.cover() != null ? openLibraryResponse.cover().large() : null)
                .available(true)
                .build();
    }
}

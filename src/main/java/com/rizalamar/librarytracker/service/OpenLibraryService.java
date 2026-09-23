package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.librarytracker.config.GenreNormalizer;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorResponse;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibraryResponse;
import com.rizalamar.librarytracker.dto.openlibrary.WorkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private final RestTemplate restTemplate;
    private final GenreNormalizer genreNormalizer;
    private final ObjectMapper objectMapper;

    private static final String ISBN_URL = "https://openlibrary.org/isbn/%s.json";
    private static final String KEY_URL = "https://openlibrary.org%s.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/%d-L.jpg";
    private static final String USER_AGENT = "LibraryTracker/1.0 (rizalamarulloh2014@gmail.com)";

    @Cacheable(value = "bookMetadata", key = "#isbn")
    public BookResponse fetchBookByIsbn(String isbn){
        String cleanIsbn = isbn.replace("-", "").trim();
        String url = String.format(ISBN_URL, cleanIsbn);

        OpenLibraryResponse edition = get(url, OpenLibraryResponse.class);

        if(edition == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in Open Library");
        }

        log.info("Raw edition for ISBN {}: {}", isbn, edition);

        WorkResponse work = fetchWork(edition.works());
        List<String> cleanGenres = work != null
                ? genreNormalizer.normalize(nullSafe(work.subjects()))
                : List.of();

        return BookResponse.builder()
                .title(edition.title())
                .isbn(isbn)
                .description(edition.descriptionText())
                .authors(resolveAuthors(edition.authors()))
                .publishers(nullSafe(edition.publishers()))
                .number_of_pages(edition.number_of_pages())
                .physicalFormat(edition.physical_format())
                .languages(edition.languagesCode())
                .publishPlaces(nullSafe(edition.publish_places()))
                .subjects(cleanGenres)
                .subjectsPeople(work != null ? nullSafe(work.subject_people()) : List.of())
                .subjectPlaces(work != null ? nullSafe(work.subject_places()) : List.of())
                .subjectTimes(work != null ? nullSafe(work.subject_times()) : List.of())
                .publishedDate(edition.publish_date())
                .imageUrl(buildCoverUrl(edition.covers()))
                .available(true)
                .build();


    }

    private WorkResponse fetchWork(List<OpenLibraryResponse.Ref> works){
        if(works == null || works.isEmpty() || works.getFirst().key() == null) return null;
        return get(String.format(KEY_URL, works.getFirst().key()), WorkResponse.class);
    }

    private List<BookResponse.Author> resolveAuthors(List<OpenLibraryResponse.Ref> refs){
        if(refs == null) return List.of();

        List<BookResponse.Author> result = new ArrayList<>();
        for(OpenLibraryResponse.Ref ref : refs){
            if(ref.key() == null) continue;
            AuthorResponse author = get(String.format(KEY_URL, ref.key()), AuthorResponse.class);
            String name = author != null ? author.name() : null;
            result.add(new BookResponse.Author(name, "https://openlibrary.org" + ref.key()));
        }
        return result;
    }

    private String buildCoverUrl(List<Integer> covers){
        if(covers == null) return null;

        return covers.stream()
                .filter(id -> id != null && id > 0)
                .findFirst()
                .map(id -> String.format(COVER_URL, id))
                .orElse(null);
    }

    private <T> T get(String url, Class<T> type){
     HttpHeaders headers = new HttpHeaders();
     headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
     try{
         ResponseEntity<T> response = restTemplate.exchange(
                 url, HttpMethod.GET, new HttpEntity<>(headers), type
         );

         return response.getBody();
     } catch (Exception e){
         log.warn("Open Library call failed for {}: {}", url, e.getMessage());
         return null;
     }
    }

    private static List<String> nullSafe(List<String> list){
        return list != null ? list : List.of();
    }
}

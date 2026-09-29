package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rizalamar.librarytracker.config.GenreNormalizer;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private final RestTemplate restTemplate;
    private final GenreNormalizer genreNormalizer;

    private static final String ISBN_URL = "https://openlibrary.org/isbn/%s.json";
    private static final String KEY_URL = "https://openlibrary.org%s.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/%d-L.jpg";
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String PREVIEW_URL = "https://openlibrary.org/api/books";
    private static final String USER_AGENT = "LibraryTracker/1.0 (rizalamarulloh2014@gmail.com)";
    private static final String FULL_PREVIEW = "full";
    private static final String PUBLIC_EBOOK_ACCESS = "public";
    private static final int MAX_IDENTIFIER_LOOKUPS = 10;

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

        List<OpenLibraryResponse.Ref> authorRefs = edition.authors();
        if((authorRefs == null || authorRefs.isEmpty()) && work != null ){
            authorRefs = work.authorRefs();
        }

        String description = edition.descriptionText() != null
                ? edition.descriptionText()
                : (work != null ? work.descriptionText() : null);

        String imageUrl = buildCoverUrl(edition.covers());
        if(imageUrl == null && work != null){
            imageUrl = buildCoverUrl(work.covers());
        }

        return BookResponse.builder()
                .title(edition.title())
                .isbn(isbn)
                .description(description)
                .authors(resolveAuthors(authorRefs))
                .publishers(nullSafe(edition.publishers()))
                .number_of_pages(edition.number_of_pages())
                .physicalFormat(edition.physical_format())
                .languages(toLanguageNames(edition.languagesCode()))
                .publishPlaces(nullSafe(edition.publish_places()))
                .subjects(cleanGenres)
                .subjectsPeople(work != null ? nullSafe(work.subject_people()) : List.of())
                .subjectPlaces(work != null ? nullSafe(work.subject_places()) : List.of())
                .subjectTimes(work != null ? nullSafe(work.subject_times()) : List.of())
                .publishedDate(edition.publish_date())
                .imageUrl(imageUrl)
                .available(true)
                .build();


    }

    public ReadableBookSearchResponse searchReadableBooks(String query, int page, int limit){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(SEARCH_URL)
                .queryParam("q", query + " ebook_access:public")
                .queryParam("page", page)
                .queryParam("limit", limit)
                .queryParam(
                        "fields",
                        "key,title,author_name,cover_i,cover_edition_key,edition_key,isbn,ebook_access,availability"
                );

        URI uri = builder.encode().build().toUri();

        OpenLibrarySearchResponse response = get(uri, OpenLibrarySearchResponse.class);
        if(response == null || response.numFound() == null){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Open Library search is unavailable");
        }

        List<ReadableBookSearchResponse.Book> books = response.docs() == null
                ? List.of()
                : response.docs().stream()
                .filter(this::isPublicReadableCandidate)
                .map(this::toReadableCandidate)
                .toList();

        return new ReadableBookSearchResponse(
                query,
                page,
                limit,
                response.numFound(),
                response.start() != null && response.start() + limit < response.numFound(),
                books
        );
    }

    private boolean isPublicReadableCandidate(OpenLibrarySearchResponse.Document doc){
        if(!PUBLIC_EBOOK_ACCESS.equalsIgnoreCase(doc.ebook_access())) return  false;
        return availabilityStatus(doc.availability())
                .map(
                        status -> !status.equalsIgnoreCase("lendable")
                        && !status.equalsIgnoreCase("checked_out")
                        && !status.equalsIgnoreCase("restricted")
                )
                .orElse(true);
    }

    private ReadableBookSearchResponse.Book toReadableCandidate(OpenLibrarySearchResponse.Document doc){
        String editionKey = pickEditionKey(doc);
        String isbn = pickIsbn(doc);
        String readerUrl = buildReadableUrl(editionKey, isbn);
        String imageUrl = doc.cover_i() != null && doc.cover_i() > 0
                ? String.format(COVER_URL, doc.cover_i())
                : null;

        return new ReadableBookSearchResponse.Book(
                editionKey,
                isbn,
                doc.title(),
                doc.author_name() != null ? doc.author_name() : List.of(),
                imageUrl,
                readerUrl != null,
                readerUrl
        );
    }

    private Optional<String> availabilityStatus(JsonNode availability){
        if(availability == null) return Optional.empty();
        JsonNode status = availability.get("status");
        if(status == null || !status.isTextual()) return Optional.empty();
        return Optional.of(status.asText());
    }

    private String pickEditionKey(OpenLibrarySearchResponse.Document doc){
        if(doc.cover_edition_key() != null && !doc.cover_edition_key().isBlank()){
            return doc.cover_edition_key();
        }
        if(doc.edition_key() == null) return null;

        return doc.edition_key().stream()
                .filter(key -> key != null && key.matches("^OL\\d+M$"))
                .findFirst()
                .orElse(null);
    }

    private String pickIsbn(OpenLibrarySearchResponse.Document doc){
        if(doc.isbn() == null) return null;

        return doc.isbn().stream()
                .filter(isbn -> isbn != null && isbn.matches("^(?:97[89])?\\d{9}[0-9Xx]$"))
                .findFirst()
                .orElse(null);
    }

    private String buildReadableUrl(String editionKey, String isbn){
        if(editionKey != null && editionKey.matches("^OL\\d+M$")){
            return "https://openlibrary.org/books/" + editionKey;
        }

        if(isbn != null){
            return "https://openlibrary.org/isbn/" + isbn;
        }

        return null;
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

    private <T> T get(URI uri, Class<T> type){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        try{
            ResponseEntity<T> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), type
            );
            return  response.getBody();
        } catch (RestClientException e){
            log.warn("Open Library call failed for {}: {} ", uri, e.getMessage());
            return null;
        }
    }

    private static List<String> nullSafe(List<String> list){
        return list != null ? list : List.of();
    }

    private static  List<String> toLanguageNames(List<String> codes){
        return codes.stream()
                .map(code -> Locale.forLanguageTag(code).getDisplayLanguage(Locale.ENGLISH))
                .filter(name -> !name.isBlank())
                .toList();
    }
}

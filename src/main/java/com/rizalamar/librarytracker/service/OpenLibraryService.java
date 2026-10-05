package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.config.GenreNormalizer;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private final GenreNormalizer genreNormalizer;
    private final OpenLibraryClient client;
    private final BookMetadataMapper bookMetadataMapper;
    private final ReadableBookMapper readableBookMapper;
    private final AuthorMapper authorMapper;


    @Cacheable(value = "bookMetadata", key = "#isbn")
    public BookResponse fetchBookByIsbn(String isbn) {
        String cleanIsbn = isbn.replace("-", "").trim();

        OpenLibraryResponse edition = client.get(
                String.format(OpenLibraryClient.ISBN_URL, cleanIsbn),
                OpenLibraryResponse.class
        );

        if (edition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in Open Library");
        }

        log.info("Raw edition for ISBN {}: {}", isbn, edition);

        WorkResponse work = bookMetadataMapper.fetchWork(edition.works());

        List<String> cleanGenres = work != null
                ? genreNormalizer.normalize(bookMetadataMapper.nullSafe(work.subjects()))
                : List.of();

        List<OpenLibraryResponse.Ref> authorRefs = edition.authors();
        if ((authorRefs == null || authorRefs.isEmpty()) && work != null) {
            authorRefs = work.authorRefs();
        }

        String description = edition.descriptionText() != null
                ? edition.descriptionText()
                : (work != null ? work.descriptionText() : null);

        String imageUrl = bookMetadataMapper.buildCoverUrl(edition.covers());
        if (imageUrl == null && work != null) {
            imageUrl = bookMetadataMapper.buildCoverUrl(work.covers());
        }

        return BookResponse.builder()
                .title(edition.title())
                .isbn(isbn)
                .description(description)
                .authors(bookMetadataMapper.resolveAuthors(authorRefs))
                .publishers(bookMetadataMapper.nullSafe(edition.publishers()))
                .number_of_pages(edition.number_of_pages())
                .physicalFormat(edition.physical_format())
                .languages(bookMetadataMapper.toLanguageNames(edition.languagesCode()))
                .publishPlaces(bookMetadataMapper.nullSafe(edition.publish_places()))
                .subjects(cleanGenres)
                .subjectsPeople(work != null ? bookMetadataMapper.nullSafe(work.subject_people()) : List.of())
                .subjectPlaces(work != null ? bookMetadataMapper.nullSafe(work.subject_places()) : List.of())
                .subjectTimes(work != null ? bookMetadataMapper.nullSafe(work.subject_times()) : List.of())
                .publishedDate(edition.publish_date())
                .imageUrl(imageUrl)
                .available(true)
                .build();


    }

    public ReadableBookSearchResponse searchReadableBooks(String query, int page, int limit) {
        URI uri = UriComponentsBuilder.fromUriString(OpenLibraryClient.SEARCH_URL)
                .queryParam("q", query + " ebook_access:public")
                .queryParam("page", page)
                .queryParam("limit", limit)
                .queryParam(
                        "fields",
                        "key,title,author_name,cover_i,cover_edition_key,edition_key,isbn,ebook_access,availability"
                )
                .encode()
                .build()
                .toUri();


        OpenLibrarySearchResponse response = client.get(uri, OpenLibrarySearchResponse.class);
        if (response == null || response.numFound() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Open Library search is unavailable");
        }

        List<ReadableBookSearchResponse.Book> books = response.docs() == null
                ? List.of()
                : response.docs().stream()
                .filter(readableBookMapper::isPublicReadableCandidate)
                .map(readableBookMapper::toReadableCandidate)
                .filter(Objects::nonNull)
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

    public List<AuthorDetailResponse> getPopularAuthors(int limit) {
        URI trendingUri = UriComponentsBuilder.fromUriString(OpenLibraryClient.TRENDING_URL)
                .queryParam("limit", limit * 2)
                .build()
                .toUri();

        TrendingBooksResponse trending = client.get(trendingUri, TrendingBooksResponse.class);
        if (trending == null || trending.works() == null) {
            return List.of();
        }

        List<String> authorKeys = trending.works().stream()
                .filter(w -> w.author_key() != null && !w.author_key().isEmpty())
                .flatMap(w -> w.author_key().stream())
                .filter(key -> key != null && !key.isBlank())
                .distinct()
                .limit(limit)
                .toList();

        List<AuthorDetailResponse> authors = new ArrayList<>();
        for (String authorKey : authorKeys) {
            OpenLibraryAuthorResponse author = client.get(
                    String.format(OpenLibraryClient.KEY_URL, "/authors/" + authorKey),
                    OpenLibraryAuthorResponse.class
            );
            if (author != null) {
                URI searchUri = UriComponentsBuilder.fromUriString(OpenLibraryClient.SEARCH_URL)
                        .queryParam("author", authorKey)
                        .queryParam("limit", 5)
                        .queryParam("fields", "key,title,first_publish_year,cover_i")
                        .build()
                        .toUri();

                OpenLibrarySearchResponse searchResponse = client.get(searchUri, OpenLibrarySearchResponse.class);
                authors.add(authorMapper.toAuthorDetail(author, searchResponse));
            }
        }

        return authors;
    }
}

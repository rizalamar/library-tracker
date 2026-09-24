package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.Author;
import com.rizalamar.librarytracker.domain.Book;
import com.rizalamar.librarytracker.domain.Publisher;
import com.rizalamar.librarytracker.dto.book.BookRequest;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.book.GenreResponse;
import com.rizalamar.librarytracker.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final OpenLibraryService openLibraryService;

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks(String genre) {
        List<Book> books = bookRepository.findAll();

        List<BookResponse> allEnrichmentBooks = books.stream()
                .map(this::mapToEnrichmentResponse)
                .toList();

        if (genre != null && !genre.isEmpty()) {
            return allEnrichmentBooks.stream()
                    .filter(b -> b.subjects() != null && b.subjects().contains(genre))
                    .collect(Collectors.toList());
        }

        return allEnrichmentBooks;
    }

    @Transactional(readOnly = true)
    public List<GenreResponse> getGenreCounts() {
        List<Book> books = bookRepository.findAll();
        List<BookResponse> allEnrichmentBooks = books.stream()
                .map(this::mapToEnrichmentResponse)
                .toList();

        Map<String, Long> genreCounter = allEnrichmentBooks.stream()
                .flatMap(bookResponse -> bookResponse.subjects() != null
                        ? bookResponse.subjects().stream()
                        : Stream.<String>empty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return genreCounter.entrySet().stream()
                .map(entry -> new GenreResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse getById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return mapToEnrichmentResponse(book);
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .authors(request.authors().stream()
                        .map(author ->
                                Author.builder()
                                        .url(author.url())
                                        .name(author.name())
                                        .build()
                        ).collect(Collectors.toSet())
                )
                .isbn(request.isbn())
                .publishers(
                        request.publishers().stream()
                                .map(publisher ->
                                        Publisher.builder()
                                                .name(publisher.name())
                                                .build()
                                ).collect(Collectors.toSet())
                )
                .publishedDate(request.publishedDate())
                .imageUrl(request.imageUrl())
                .available(true)
                .build();
        Book savedBook = bookRepository.save(book);
        return mapToEnrichmentResponse(savedBook);
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (Objects.nonNull(request.title())) {
            book.setTitle(request.title());
        }

        if (Objects.nonNull(request.authors())) {
            book.setAuthors(
                    request.authors().stream()
                            .map(author ->
                                    Author.builder()
                                            .url(author.url())
                                            .name(author.name())
                                            .build()
                            ).collect(Collectors.toSet())
            );
        }

        if (Objects.nonNull(request.isbn())) {
            book.setIsbn(request.isbn());
        }

        if (Objects.nonNull(request.publishers())) {
            book.setPublishers(
                    request.publishers().stream()
                            .map(publisher ->
                                    Publisher.builder()
                                            .name(publisher.name())
                                            .build()
                            ).collect(Collectors.toSet())
            );
        }

        if (Objects.nonNull(request.publishedDate())) {
            book.setPublishedDate(request.publishedDate());
        }

        if (Objects.nonNull(request.imageUrl())) {
            book.setImageUrl(request.imageUrl());
        }

        Book updatedBook = bookRepository.save(book);
        return mapToEnrichmentResponse(updatedBook);
    }

    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        bookRepository.deleteById(book.getId());
    }

    public BookResponse mapToEnrichmentResponse(Book book) {
        BookResponse baseBookResponse = mapToBookResponse(book);

        try {
            BookResponse enriched = openLibraryService.fetchBookByIsbn(book.getIsbn());

            if (enriched == null) return baseBookResponse;

            return baseBookResponse.toBuilder()
                    .number_of_pages(enriched.number_of_pages())
                    .description(enriched.description())
                    .physicalFormat(enriched.physicalFormat())
                    .languages(enriched.languages() != null ? enriched.languages() : List.of())
                    .publishPlaces(enriched.publishPlaces() != null ? enriched.publishPlaces() : List.of())
                    .subjects(enriched.subjects() != null ? enriched.subjects() : List.of())
                    .subjectPlaces(enriched.subjectPlaces() != null ? enriched.subjectPlaces() : List.of())
                    .subjectsPeople(enriched.subjectsPeople() != null ? enriched.subjectsPeople() : List.of())
                    .subjectTimes(enriched.subjectTimes() != null ? enriched.subjectTimes() : List.of())
                    .build();
        } catch (Exception e) {
            System.out.printf("Enrichment data failed %s: %s", book.getIsbn(), e.getMessage());
            return baseBookResponse;
        }
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authors(
                        book.getAuthors() != null ?
                                book.getAuthors().stream().map(author ->
                                        new BookResponse.Author(author.getName(), author.getUrl())
                                ).toList()
                                : List.of()
                )
                .isbn(book.getIsbn())
                .publishers(book.getPublishers() != null
                        ? book.getPublishers().stream().map(Publisher::getName).toList()
                        : List.of())
                .publishedDate(book.getPublishedDate())
                .imageUrl(book.getImageUrl())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .build();
    }
}

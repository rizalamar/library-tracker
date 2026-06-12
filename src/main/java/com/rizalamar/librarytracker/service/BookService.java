package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.Book;
import com.rizalamar.librarytracker.dto.book.BookRequest;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.repository.BookRepository;
import com.rizalamar.librarytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks(){
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse getById(UUID id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return mapToResponse(book);
    }

    @Transactional
    public BookResponse createBook(BookRequest request){
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .isbn(request.isbn())
                .description(request.description())
                .publisher(request.publisher())
                .publishedDate(request.publishedDate())
                .imageUrl(request.imageUrl())
                .available(true)
                .build();
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest request){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if(Objects.nonNull(request.title())){
            book.setTitle(request.title());
        }

        if(Objects.nonNull(request.author())){
            book.setAuthor(request.author());
        }

        if(Objects.nonNull(request.isbn())){
            book.setIsbn(request.isbn());
        }

        if(Objects.nonNull(request.description())){
            book.setDescription(request.description());
        }

        if(Objects.nonNull(request.publisher())){
            book.setPublisher(request.publisher());
        }

        if(Objects.nonNull(request.publishedDate())){
            book.setPublishedDate(request.publishedDate());
        }

        if(Objects.nonNull(request.imageUrl())){
            book.setImageUrl(request.imageUrl());
        }

        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook);
    }

    public void deleteBook(UUID id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        bookRepository.deleteById(book.getId());
    }

    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .imageUrl(book.getImageUrl())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .build();
    }
}

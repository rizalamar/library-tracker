package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.book.BookRequest;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public WebResponse<List<BookResponse>> getAllBooks(){
        List<BookResponse> allBooks = bookService.getAllBooks();
        return WebResponse.<List<BookResponse>>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(allBooks)
                .build();
    }

    @GetMapping("/{bookId}")
    public WebResponse<BookResponse> getBookById(@PathVariable("bookId") UUID id){
        BookResponse bookResponse = bookService.getById(id);
        return WebResponse.<BookResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(bookResponse)
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WebResponse<BookResponse> createBook(@Valid @RequestBody BookRequest request){
        BookResponse book = bookService.createBook(request);
        return WebResponse.<BookResponse>builder()
                .code(HttpStatus.CREATED.value())
                .status("CREATED")
                .data(book)
                .build();
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public WebResponse<BookResponse> updateBook(
            @PathVariable("bookId") UUID id,
            @Valid @RequestBody BookRequest request
    ){
        BookResponse bookResponse = bookService.updateBook(id, request);
        return WebResponse.<BookResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(bookResponse)
                .build();
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public WebResponse<String> deleteBook(@PathVariable("bookId") UUID id){
        bookService.deleteBook(id);
        return WebResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data("Book deleted successful")
                .build();
    }
}

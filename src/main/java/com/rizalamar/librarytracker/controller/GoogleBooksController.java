package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.googlebooks.GoogleBooksResponse;
import com.rizalamar.librarytracker.service.GoogleBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/googlebooks")
@RequiredArgsConstructor
public class GoogleBooksController {
    private final GoogleBookService googleBookService;

    @GetMapping("/{isbn}")
    public ResponseEntity<WebResponse<BookResponse>> fetchBookByIsbn(@PathVariable("isbn") String isbn){
        BookResponse bookResponse = googleBookService.fetchBookByIsbn(isbn);
        return ResponseEntity.ok(
                WebResponse.<BookResponse>builder()
                        .code(HttpStatus.OK.value()).status("OK").data(bookResponse).build()
        );
    }
}

package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.service.OpenLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external-books")
@RequiredArgsConstructor
public class OpenLibraryController {
    private final OpenLibraryService openLibraryService;

    @GetMapping("/{isbn}")
    public ResponseEntity<WebResponse<BookResponse>> fetchBookByIsbn(@PathVariable String isbn){
        BookResponse bookResponse = openLibraryService.fetchBookByIsbn(isbn);
        return ResponseEntity.ok(
                WebResponse.<BookResponse>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(bookResponse)
                        .build()
        );
    }
}

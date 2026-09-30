package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.ReadableBookSearchResponse;
import com.rizalamar.librarytracker.service.OpenLibraryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/external-books")
@RequiredArgsConstructor
@Validated
public class OpenLibraryController {
    private final OpenLibraryService openLibraryService;

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebResponse<ReadableBookSearchResponse>> searchReadableBooks(
            @RequestParam @NotBlank @Size(max = 120) String q,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(20) int limit
    ){
        ReadableBookSearchResponse response = openLibraryService.searchReadableBooks(q, page, limit);
        return ResponseEntity.ok(
                WebResponse.<ReadableBookSearchResponse>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
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

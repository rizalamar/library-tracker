package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorDetailResponse;
import com.rizalamar.librarytracker.service.OpenLibraryService;
import jakarta.validation.constraints.Max;
import java.util.List;
import jakarta.validation.constraints.Min;
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

    @GetMapping("/authors/popular")
    public ResponseEntity<WebResponse<List<AuthorDetailResponse>>> getPopularAuthors(
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit
    ){
        List<AuthorDetailResponse> authors = openLibraryService.getPopularAuthors(limit);
        return ResponseEntity.ok(
                WebResponse.<List<AuthorDetailResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(authors)
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

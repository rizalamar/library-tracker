package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.mybook.MyBookRequest;
import com.rizalamar.librarytracker.dto.mybook.MyBookResponse;
import com.rizalamar.librarytracker.security.CurrentUser;
import com.rizalamar.librarytracker.service.MyBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/my-books")
@RequiredArgsConstructor
public class MyBookController {
    private final MyBookService myBookService;

    @GetMapping
    public ResponseEntity<WebResponse<List<MyBookResponse>>> getMyBooks(@CurrentUser User user) {
        List<MyBookResponse> myBooksResponse = myBookService.getMyBooks(user);
        return ResponseEntity.ok(
                WebResponse.<List<MyBookResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(myBooksResponse)
                        .build()
        );
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<WebResponse<MyBookResponse>> addBookToCollection(
            @CurrentUser User user,
            @PathVariable UUID bookId
            ) {
        MyBookResponse myBookResponse = myBookService.addBookToCollection(user, bookId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        WebResponse.<MyBookResponse>builder()
                                .code(HttpStatus.CREATED.value())
                                .status("CREATED")
                                .data(myBookResponse)
                                .build()
                );
    }

    @PutMapping("/{myBookId}")
    public ResponseEntity<WebResponse<MyBookResponse>> updateMyBook(
            @CurrentUser User user,
            @PathVariable UUID myBookId,
            @Valid @RequestBody MyBookRequest request
            ) {
        MyBookResponse myBookResponse = myBookService.updateMyBook(user, myBookId, request);
        return ResponseEntity.ok(
                WebResponse.<MyBookResponse>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(myBookResponse)
                        .build()
        );
    }

    @DeleteMapping("/{myBookId}")
    public ResponseEntity<WebResponse<String>> removeBookFromCollection(
            @CurrentUser User user,
            @PathVariable UUID myBookId
    ){
        myBookService.removeBookFromCollection(user, myBookId);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data("Book removed from collection")
                        .build()
        );
    }
}

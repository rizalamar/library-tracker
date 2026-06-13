package com.rizalamar.librarytracker.dto.mybook;

import com.rizalamar.librarytracker.domain.ReadingStatus;
import com.rizalamar.librarytracker.dto.book.BookRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MyBookRequest(
    ReadingStatus status,
    String notes
) {
}

package com.rizalamar.librarytracker.dto.mybook;

import com.rizalamar.librarytracker.domain.ReadingStatus;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MyBookResponse(
        UUID id,
        BookResponse book,
        ReadingStatus status,
        String notes,
        LocalDateTime createdAt
) {
}

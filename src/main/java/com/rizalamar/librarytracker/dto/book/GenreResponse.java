package com.rizalamar.librarytracker.dto.book;

import lombok.Builder;

@Builder
public record GenreResponse(
        String name,
        long count
) {
}

package com.rizalamar.librarytracker.dto;

import lombok.Builder;

@Builder
public record WebResponse<T>(
        Integer code,
        String status,
        T data
){}

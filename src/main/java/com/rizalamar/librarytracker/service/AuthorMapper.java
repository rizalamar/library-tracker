package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorDetailResponse;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibraryAuthorResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AuthorMapper {
    public AuthorDetailResponse toAuthorDetail(OpenLibraryAuthorResponse source){
        String bioText = extractText(source.bio());

        List<String> photoUrls = source.photos() == null || source.photos().isEmpty()
                ? List.of()
                : source.photos().stream()
                .filter(Objects::nonNull)
                .map(id -> String.format(OpenLibraryClient.COVER_URL, id))
                .toList();

        return AuthorDetailResponse.builder()
                .name(source.name())
                .personalName(source.personalName())
                .fullerName(source.fullerName())
                .birthDate(source.birthDate())
                .bio(bioText)
                .photos(photoUrls)
                .links(source.links() != null ? source.links() : List.of())
                .alternateNames(source.alternateNames() != null ? source.alternateNames() : List.of())
                .topWorks(List.of())
                .build();
    }

    public String extractText(JsonNode node){
        if(node == null) return null;
        if(node.isTextual()) return node.asText();
        if(node.has("value")) return node.get("value").asText();
        return null;
    }
}

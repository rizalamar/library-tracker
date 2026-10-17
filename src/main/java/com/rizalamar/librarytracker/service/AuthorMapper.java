package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorDetailResponse;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorWorksResponse;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibraryAuthorResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AuthorMapper {
    public AuthorDetailResponse toAuthorDetail(OpenLibraryAuthorResponse source, AuthorWorksResponse worksResponse){
        String bioText = extractText(source.bio());

        List<String> photoUrls = source.photos() == null || source.photos().isEmpty()
                ? List.of()
                : source.photos().stream()
                .filter(Objects::nonNull)
                .map(id -> String.format(OpenLibraryClient.COVER_URL, id))
                .toList();

        List<AuthorDetailResponse.AuthorWork> topWorks = worksResponse == null || worksResponse.entries() == null
                ? List.of()
                : worksResponse.entries().stream()
                .limit(5)
                .map(work -> {
                    String coverUrl = null;
                    if (work.covers() != null && !work.covers().isEmpty()) {
                        Integer coverId = work.covers().getFirst();
                        if (coverId != null && coverId > 0) {
                            coverUrl = String.format(OpenLibraryClient.COVER_URL, coverId);
                        }
                    }
                    return AuthorDetailResponse.AuthorWork.builder()
                            .key(work.key())
                            .title(work.title())
                            .firstPublishYear(work.getPublishYearString())
                            .coverUrl(coverUrl)
                            .build();
                })
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
                .topWorks(topWorks)
                .build();
    }

    public String extractText(JsonNode node){
        if(node == null) return null;
        if(node.isTextual()) return node.asText();
        if(node.has("value")) return node.get("value").asText();
        return null;
    }
}

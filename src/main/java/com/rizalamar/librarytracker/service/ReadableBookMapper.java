package com.rizalamar.librarytracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibrarySearchResponse;
import com.rizalamar.librarytracker.dto.openlibrary.ReadableBookSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadableBookMapper {
    private final OpenLibraryClient client;

    private static final String FULL_PREVIEW = "full";
    private static final String PUBLIC_EBOOK_ACCESS = "public";

    public boolean isPublicReadableCandidate(OpenLibrarySearchResponse.Document doc){
        if(!PUBLIC_EBOOK_ACCESS.equalsIgnoreCase(doc.ebook_access())) return  false;
        return availabilityStatus(doc.availability())
                .map(
                        status -> !status.equalsIgnoreCase("lendable")
                                && !status.equalsIgnoreCase("checked_out")
                                && !status.equalsIgnoreCase("restricted")
                )
                .orElse(true);
    }

    public ReadableBookSearchResponse.Book toReadableCandidate(OpenLibrarySearchResponse.Document doc){
        String editionKey = pickEditionKey(doc);
        String isbn = pickIsbn(doc);
        String readerUrl = fetchVerifiedReaderUrl(editionKey, isbn);
        if(readerUrl == null) return null;

        String imageUrl = doc.cover_i() != null && doc.cover_i() > 0
                ? String.format(OpenLibraryClient.COVER_URL, doc.cover_i())
                : null;

        return new ReadableBookSearchResponse.Book(
                editionKey,
                isbn,
                doc.title(),
                doc.author_name() != null ? doc.author_name() : List.of(),
                imageUrl,
                true,
                readerUrl
        );
    }

    public Optional<String> availabilityStatus(JsonNode availability){
        if(availability == null) return Optional.empty();
        JsonNode status = availability.get("status");
        if(status == null || !status.isTextual()) return Optional.empty();
        return Optional.of(status.asText());
    }

    public String pickEditionKey(OpenLibrarySearchResponse.Document doc){
        if(doc.cover_edition_key() != null && !doc.cover_edition_key().isBlank()){
            return doc.cover_edition_key();
        }
        if(doc.edition_key() == null) return null;

        return doc.edition_key().stream()
                .filter(key -> key != null && key.matches("^OL\\d+M$"))
                .findFirst()
                .orElse(null);
    }

    public String pickIsbn(OpenLibrarySearchResponse.Document doc){
        if(doc.isbn() == null) return null;

        return doc.isbn().stream()
                .filter(isbn -> isbn != null && isbn.matches("^(?:97[89])?\\d{9}[0-9Xx]$"))
                .findFirst()
                .orElse(null);
    }

    public String fetchVerifiedReaderUrl(String editionKey, String isbn){
        if(editionKey == null && isbn == null) return null;

        String identifier = editionKey != null ? "OLID:" + editionKey : "ISBN:" + isbn;

        URI uri = UriComponentsBuilder.fromUriString(OpenLibraryClient.PREVIEW_URL)
                .queryParam("bibkeys", identifier)
                .queryParam("format", "json")
                .queryParam("jscmd", "data")
                .build()
                .toUri();

        JsonNode previewData = client.get(uri, JsonNode.class);
        if(previewData == null) return null;

        JsonNode entry = previewData.get(identifier);
        if(entry == null) return null;
        if(!FULL_PREVIEW.equalsIgnoreCase(entry.path("preview").asText(null))) return null;

        return sanitizeReaderUrl(entry.path("preview_url").asText(null));
    }

    public String sanitizeReaderUrl(String rawUrl){
        if(rawUrl == null || rawUrl.isBlank()) return null;

        try{
            URL url = URI.create(rawUrl).toURL();
            if(!"https".equalsIgnoreCase(url.getProtocol())) return null;
            if(url.getUserInfo() != null) return null;

            String host = url.getHost() == null ? "" : url.getHost().toLowerCase(Locale.ROOT);
            boolean trusted = host.equals("openlibrary.org")
                    || host.endsWith(".openlibrary.org")
                    || host.equals("archive.org")
                    || host.endsWith(".archive.org");
            if(!trusted) return null;

            return url.toString();
        } catch (Exception e){
            log.warn("Rejected untrusted reader URL: {}", rawUrl);
            return null;
        }
    }
}

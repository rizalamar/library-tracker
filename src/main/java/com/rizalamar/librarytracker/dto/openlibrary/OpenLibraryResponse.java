package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryResponse(
        String title,
        String publish_date,
        Integer number_of_pages,
        String physical_format,
        JsonNode description,
        List<Integer> covers,
        List<String> publishers,
        List<String> publish_places,
        List<Ref> languages,
        List<Ref> authors,
        List<Ref> works,
        List<String> isbn_10,
        List<String> isbn_13
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Ref(String key){}

    public String descriptionText(){
        if(description == null) return null;
        if(description.isTextual()) return description.asText();
        if(description.has("value")) return description.get("value").asText();

        return null;
    }

    public List<String> languagesCode(){
        if(languages == null) return List.of();

        return languages.stream()
                .map(language -> language.key)
                .filter(Objects::nonNull)
                .map(k -> k.substring(k.lastIndexOf('/') + 1))
                .toList();
    }
}

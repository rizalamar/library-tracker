package com.rizalamar.librarytracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkResponse(
        String title,
        JsonNode description,
        List<WorkAuthor> authors,
        List<Integer> covers,
        List<String> subjects,
        List<String> subject_places,
        List<String> subject_people,
        List<String> subject_times
) {
    public record WorkAuthor(OpenLibraryResponse.Ref author){}

    public List<OpenLibraryResponse.Ref> authorRefs(){
        if(authors == null) return List.of();
        return authors.stream()
                .map(WorkAuthor::author)
                .filter(Objects::nonNull)
                .toList();
    }

    public String descriptionText() {
        if (description == null) return null;
        if (description.isTextual()) return description.asText();
        if (description.has("value")) return description.get("value").asText();
        return null;
    }
}

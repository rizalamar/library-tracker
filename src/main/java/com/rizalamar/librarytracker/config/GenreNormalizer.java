package com.rizalamar.librarytracker.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GenreNormalizer {

    private static final Map<String, String> GENRE_MAP = new HashMap<>();

    static {
        GENRE_MAP.put("fiction", "Fiction");
        GENRE_MAP.put("novel", "Fiction");
        GENRE_MAP.put("magic", "Fantasy");
        GENRE_MAP.put("elves", "Fantasy");
        GENRE_MAP.put("detective", "Detective");
        GENRE_MAP.put("mystery", "Mistery");
        GENRE_MAP.put("history", "History");
        GENRE_MAP.put("psychology", "Psychology");
    }

    public List<String> normalize(List<String> rawSubjects){
        if(rawSubjects == null) return List.of();

        return rawSubjects.stream()
                .map(subject -> mapToCommonGenre(subject))
                .filter(subject -> Objects.nonNull(subject))
                .distinct()
                .collect(Collectors.toList());
    }

    private String mapToCommonGenre(String rawSubject){
        String lowercaseSubject = rawSubject.toLowerCase();

        for(Map.Entry<String, String> entry : GENRE_MAP.entrySet()){
            if(lowercaseSubject.contains(entry.getKey())){
                return entry.getValue();
            }
        }

        return null;
    }

}

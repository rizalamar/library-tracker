package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.AuthorResponse;
import com.rizalamar.librarytracker.dto.openlibrary.OpenLibraryResponse;
import com.rizalamar.librarytracker.dto.openlibrary.WorkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class BookMetadataMapper {
    private final OpenLibraryClient client;

    public WorkResponse fetchWork(List<OpenLibraryResponse.Ref> works){
        if(works == null || works.isEmpty() || works.getFirst().key() == null) return null;
        return client.get(String.format(OpenLibraryClient.KEY_URL, works.getFirst().key()), WorkResponse.class);
    }

    public List<BookResponse.Author> resolveAuthors(List<OpenLibraryResponse.Ref> refs){
        if(refs == null) return List.of();

        List<BookResponse.Author> result = new ArrayList<>();
        for(OpenLibraryResponse.Ref ref : refs){
            if(ref.key() == null) continue;
            AuthorResponse author = client.get(String.format(OpenLibraryClient.KEY_URL, ref.key()), AuthorResponse.class);
            String name = author != null ? author.name() : null;
            result.add(new BookResponse.Author(name, "https://openlibrary.org" + ref.key()));
        }
        return result;
    }

    public String buildCoverUrl(List<Integer> covers){
        if(covers == null) return null;

        return covers.stream()
                .filter(id -> id != null && id > 0)
                .findFirst()
                .map(id -> String.format(OpenLibraryClient.COVER_URL, id))
                .orElse(null);
    }

    public static List<String> nullSafe(List<String> list){
        return list != null ? list : List.of();
    }

    public static  List<String> toLanguageNames(List<String> codes){
        return codes.stream()
                .map(code -> Locale.forLanguageTag(code).getDisplayLanguage(Locale.ENGLISH))
                .filter(name -> !name.isBlank())
                .toList();
    }

}

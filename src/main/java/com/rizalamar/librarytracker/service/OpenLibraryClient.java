package com.rizalamar.librarytracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenLibraryClient {
    private final RestTemplate restTemplate;

    public static final String ISBN_URL = "https://openlibrary.org/isbn/%s.json";
    public static final String KEY_URL = "https://openlibrary.org%s.json";
    public static final String COVER_URL = "https://covers.openlibrary.org/b/id/%d-L.jpg";
    public static final String SEARCH_URL = "https://openlibrary.org/search.json";
    public static final String TRENDING_URL = "https://openlibrary.org/trending/monthly.json";
    public static final String PREVIEW_URL = "https://openlibrary.org/api/books";

    private static final String USER_AGENT = "LibraryTracker/1.0 (rizalamarulloh2014@gmail.com)";

    public <T> T get(String url, Class<T> type){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        try{
            ResponseEntity<T> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), type
            );

            return response.getBody();
        } catch (Exception e){
            log.warn("Open Library call failed for {}: {}", url, e.getMessage());
            return null;
        }
    }

    public <T> T get(URI uri, Class<T> type){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        try{
            ResponseEntity<T> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), type
            );
            return  response.getBody();
        } catch (RestClientException e){
            log.warn("Open Library call failed for {}: {} ", uri, e.getMessage());
            return null;
        }
    }
}

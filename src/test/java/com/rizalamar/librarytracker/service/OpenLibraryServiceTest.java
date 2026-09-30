package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.config.GenreNormalizer;
import com.rizalamar.librarytracker.dto.openlibrary.ReadableBookSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OpenLibraryServiceTest {

    private MockRestServiceServer server;
    private OpenLibraryService service;

    private static final String SEARCH_JSON = """
            {
                "numFound": 1,
                "start": 0,
                "docs" : [
                    {
                        "title": "Dune",
                        "author_name": ["Frank Herbert"],
                        "cover_i": 12345,
                        "cover_edition_key": "OL123M",
                        "ebook_access": "public",
                        "availability": {"status": "full access"}
                    }
                ]
            }
            """;

    private static final String BOOKS_JSON_FULL = """
            {
              "OLID:OL123M": {
                "preview": "full",
                "preview_url": "https://archive.org/details/dune"
              }
            }
            """;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new OpenLibraryService(
                restTemplate, new GenreNormalizer()
        );
    }

    private void expectSearch(String docsJson) {
        server.expect(requestTo(containsString("/search.json")))
                .andExpect(queryParam("q", containsString("dune")))
                .andExpect(queryParam("page", "1"))
                .andExpect(queryParam("limit", "20"))
                .andRespond(withSuccess(docsJson, MediaType.APPLICATION_JSON));
    }

    @Test
    void searchReadableBooks_mapsVerifiedPublicBook() {
        expectSearch(SEARCH_JSON);
        server.expect(requestTo(containsString("/api/books")))
                .andExpect(queryParam("bibkeys", containsString("OL123M")))
                .andRespond(withSuccess(BOOKS_JSON_FULL, MediaType.APPLICATION_JSON));

        ReadableBookSearchResponse response = service.searchReadableBooks("dune", 1, 20);

        assertThat(response.books()).hasSize(1);
        ReadableBookSearchResponse.Book book = response.books().getFirst();
        assertThat(book.title()).isEqualTo("Dune");
        assertThat(book.authors()).containsExactly("Frank Herbert");
        assertThat(book.editionKey()).isEqualTo("OL123M");
        assertThat(book.readOnline()).isTrue();
        assertThat(book.readerUrl()).isEqualTo("https://archive.org/details/dune");
        assertThat(book.imageUrl()).isEqualTo("https://covers.openlibrary.org/b/id/12345-L.jpg");
        assertThat(response.candidateTotal()).isEqualTo(1);
        server.verify();
    }

    @Test
    void searchReadableBooks_excludesBorroableCandidates() {
        expectSearch("""
                {
                    "numFound": 1,
                    "start": 0,
                    "docs": [
                        {
                            "title": "Dune",
                            "cover_edition_key": "OL123M",
                            "ebook_access" : "borrowable"
                        }
                    ]
                }
                """);

        ReadableBookSearchResponse response = service.searchReadableBooks("dune", 1, 20);

        assertThat(response.books()).isEmpty();
        assertThat(response.candidateTotal()).isEqualTo(1);
        server.verify();
    }

    @Test
    void searchReadableBooks_excludeCandidateWithoutFullPreview() {
        expectSearch(SEARCH_JSON);
        server.expect(requestTo(containsString("/api/books")))
                .andRespond(withSuccess(
                        """
                                {
                                    "OLID:OL123M": {
                                        "preview": "noview",
                                        "preview_url": ""https://archive.org/details/dune
                                    }
                                }
                                """, MediaType.APPLICATION_JSON
                ));
        assertThat(service.searchReadableBooks("dune", 1, 20).books().isEmpty());
        server.verify();
    }

    @Test
    void searchReadbleBooks_rejectUntrustedReaderUrl() {
        expectSearch(SEARCH_JSON);
        server.expect(requestTo(containsString("/api/books")))
                .andRespond(withSuccess(
                        """
                                    {"OLID:OL123M": {"preview": "full", "preview_url": "https://evil.example.com/dune"}}
                                """, MediaType.APPLICATION_JSON
                ));
        assertThat(service.searchReadableBooks("dune", 1, 20).books()).isEmpty();
        server.verify();
    }

    @Test
    void searchReadableBooks_acceptOpenLibraryReaderUrl() {
        expectSearch(SEARCH_JSON);
        server.expect(
                        requestTo(containsString("/api/books"))
                )
                .andRespond(withSuccess("""                                                                           
                        {"OLID:OL123M": {"preview": "full", "preview_url": "https://openlibrary.org/books/OL123M"}}
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.searchReadableBooks("dune", 1, 20).books()).hasSize(1);
        server.verify();
    }

    @Test
    void searchReadableBooks_fallsBackToIsbnWhenEditionKeyMissing() {
        expectSearch("""
                {"numFound": 1, "start": 0, "docs": [
                  {"title": "Dune", "isbn": ["9780441013593"], "ebook_access": "public"}
                ]}
                """);
        server.expect(requestTo(containsString("/api/books")))
                .andExpect(queryParam("bibkeys", containsString("9780441013593")))
                .andRespond(withSuccess("""
                        {"ISBN:9780441013593": {"preview": "full", "preview_url": "https://archive.org/details/dune"}}
                        """, MediaType.APPLICATION_JSON));

        ReadableBookSearchResponse response = service.searchReadableBooks("dune", 1, 20);

        assertThat(response.books()).hasSize(1);
        assertThat(response.books().getFirst().isbn()).isEqualTo("9780441013593");
        server.verify();
    }

    @Test
    void searchReadableBooks_returnsEmptyListWhenNoDocs(){
        expectSearch("{\"numFound\": 0, \"start\": 0, \"docs\": []}");

        ReadableBookSearchResponse response = service.searchReadableBooks("dune", 1, 20);

        assertThat(response.books()).isEmpty();
        assertThat(response.candidateTotal()).isZero();
        assertThat(response.hasNextCandidatePage()).isFalse();
        server.verify();
    }

    @Test
    void searchReadableBooks_returnsBadGatewayWhenSearchFails(){
        server.expect(requestTo(containsString("/search.json")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.searchReadableBooks("dune", 1, 20)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        server.verify();
    }
}
package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.config.SecurityConfig;
import com.rizalamar.librarytracker.config.Webconfig;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.openlibrary.ReadableBookSearchResponse;
import com.rizalamar.librarytracker.exception.custom.CustomAccessDeniedHandler;
import com.rizalamar.librarytracker.exception.custom.CustomAuthenticationEntryPoint;
import com.rizalamar.librarytracker.repository.UserRepository;
import com.rizalamar.librarytracker.security.CustomUserDetailService;
import com.rizalamar.librarytracker.security.JwtAuthenticationFilter;
import com.rizalamar.librarytracker.security.JwtUtil;
import com.rizalamar.librarytracker.service.OpenLibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpenLibraryController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, Webconfig.class, JwtAuthenticationFilter.class})
class OpenLibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpenLibraryService openLibraryService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    @WithMockUser
    void searchReadableBooks_shouldReturnOk() throws Exception {
        ReadableBookSearchResponse mockResponse = new ReadableBookSearchResponse(
                "dune", 1, 20, 1, false,
                List.of(new ReadableBookSearchResponse.Book(
                        "OL123M", "9780441013593", "Dune",
                        List.of("Frank Herbert"), "https://covers.openlibrary.org/b/id/12345-L.jpg",
                        true, "https://archive.org/details/dune"
                ))
        );

        given(openLibraryService.searchReadableBooks(eq("dune"), eq(1), eq(20)))
                .willReturn(mockResponse);

        mockMvc.perform(get("/api/v1/external-books/search")
                        .param("q", "dune")
                        .param("page", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.query").value("dune"))
                .andExpect(jsonPath("$.data.books[0].title").value("Dune"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fetchBookByIsbn_asAdmin_shouldReturnOk() throws Exception {
        BookResponse mockBook = BookResponse.builder()
                .title("Dune")
                .isbn("9780441013593")
                .authors(List.of(new BookResponse.Author("Frank Herbert", "https://openlibrary.org/authors/123")))
                .build();

        given(openLibraryService.fetchBookByIsbn(eq("9780441013593")))
                .willReturn(mockBook);

        mockMvc.perform(get("/api/v1/external-books/9780441013593")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.title").value("Dune"))
                .andExpect(jsonPath("$.data.isbn").value("9780441013593"));
    }
}

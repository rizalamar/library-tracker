package com.rizalamar.librarytracker.exception.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.librarytracker.dto.WebResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        WebResponse<String> webResponse = WebResponse.<String>builder()
                .code(HttpServletResponse.SC_FORBIDDEN)
                .status("FORBIDDEN")
                .data("You do not have permission to access this resource")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(webResponse));
    }
}

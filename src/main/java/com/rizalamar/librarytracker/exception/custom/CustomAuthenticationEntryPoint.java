package com.rizalamar.librarytracker.exception.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.librarytracker.dto.WebResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        WebResponse<String> webResponse = WebResponse.<String>builder()
                .code(HttpServletResponse.SC_UNAUTHORIZED)
                .status("UNAUTHORIZED")
                .data("You are not authenticated")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(webResponse));
    }
}

package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.auth.AuthResponse;
import com.rizalamar.librarytracker.dto.auth.LoginRequest;
import com.rizalamar.librarytracker.dto.auth.RegisterRequest;
import com.rizalamar.librarytracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public WebResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse response = authService.register(request);
        return WebResponse.<AuthResponse>builder()
                .code(HttpStatus.CREATED.value())
                .status("CREATED")
                .data(response)
                .build();
    }

    @PostMapping("/login")
    public WebResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return WebResponse.<AuthResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }
}

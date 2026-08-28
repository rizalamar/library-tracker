package com.rizalamar.librarytracker.controller;

import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.dto.WebResponse;
import com.rizalamar.librarytracker.dto.user.UpdateEmailRequest;
import com.rizalamar.librarytracker.dto.user.UserResponse;
import com.rizalamar.librarytracker.security.CurrentUser;
import com.rizalamar.librarytracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<WebResponse<UserResponse>> getCurrentUser(@CurrentUser User currentUser){
        UserResponse profile = userService.getProfile(currentUser);
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .code(HttpStatus.OK.value())
                        .status("OK")
                        .data(profile)
                        .build()
        );
    }

    @PutMapping("/me/email")
    public ResponseEntity<WebResponse<UserResponse>> updateEmail(
            @CurrentUser User currentUser,
            @Valid @RequestBody UpdateEmailRequest request
    ) {
        UserResponse userResponse = userService.updateEmail(currentUser.getId(), request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<UserResponse>builder()
                                .code(HttpStatus.OK.value())
                                .status("OK")
                                .data(userResponse)
                                .build()
                );
    }

    @DeleteMapping("/me")
    public ResponseEntity<WebResponse<String>> deleteAccount(@CurrentUser User currentUser){
        userService.deleteAccount(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<String>builder()
                                .code(HttpStatus.OK.value())
                                .status("OK")
                                .data("Account deleted successfully")
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<List<UserResponse>>> getAllUser(){
        List<UserResponse> allUsers = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        WebResponse.<List<UserResponse>>builder()
                                .code(HttpStatus.OK.value())
                                .status("OK")
                                .data(allUsers)
                                .build()
                );
    }

}

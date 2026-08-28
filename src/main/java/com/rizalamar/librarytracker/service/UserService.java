package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.dto.user.UpdateEmailRequest;
import com.rizalamar.librarytracker.dto.user.UserResponse;
import com.rizalamar.librarytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private UserResponse mapToResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(), 
                user.getRole()
        );
    }

    public UserResponse getProfile(User user){
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateEmail(UUID userId, UpdateEmailRequest request){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        if(Objects.nonNull(request.email())){
            user.setEmail(request.email());
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public void deleteAccount(UUID userId){
        userRepository.deleteById(userId);
    }

    public List<UserResponse> findAllUsers(){
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}

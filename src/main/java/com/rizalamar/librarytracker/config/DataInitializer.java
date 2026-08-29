package com.rizalamar.librarytracker.config;

import com.rizalamar.librarytracker.domain.Role;
import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")){
            User admin = User.builder()
                    .username("admin")
                    .email("admin@library.com")
                    .fullName("Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Hai admin! Welcome!");
        }
    }
}

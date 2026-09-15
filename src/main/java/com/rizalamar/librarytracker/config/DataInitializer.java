package com.rizalamar.librarytracker.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizalamar.librarytracker.domain.*;
import com.rizalamar.librarytracker.repository.BookRepository;
import com.rizalamar.librarytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {
        User admin = userRepository.findByUsername("admin").orElse(null);

        if (!userRepository.existsByUsername("admin")){
            admin = User.builder()
                    .username("admin")
                    .email("admin@library.com")
                    .fullName("Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin created");
        } else if (admin.getEmail() == null || admin.getFullName() == null){
            admin.setFullName("Administrator");
            admin.setEmail("admin@library.com");
            userRepository.save(admin);
            log.info("Admin update with full details");
        }

        if(bookRepository.count() == 0) {
            InputStream is = new ClassPathResource("books.json").getInputStream();
            Set<Book> books = mapper.readValue(is, new TypeReference<>() {
            });

            bookRepository.saveAll(books);
            log.info("Initialized {} books from JSON.", books.size());
        }
    }
}

package com.rizalamar.librarytracker.config;

import com.rizalamar.librarytracker.domain.*;
import com.rizalamar.librarytracker.repository.BookRepository;
import com.rizalamar.librarytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
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

        if(bookRepository.count() == 0) {
            Book book1 = Book.builder()
                    .title("Clean Code")
                    .isbn("978-0132350884")
                    .authors(List.of(
                            Author.builder()
                                    .name("Robert C. Martin")
                                    .url("http://openlibrary.org/authors/OL2653686A/Robert_C._Martin")
                                    .build()
                            )
                    )
                    .publishers(List.of(
                            Publisher.builder()
                                    .name("Prentice Hall")
                                    .build()
                            )
                    )
                    .subtitle("A Handbook of Agile Software Craftsmanship")
                    .publishedDate("July 2008")
                    .imageUrl("https://covers.openlibrary.org/b/id/15126503-L.jpg")
                    .build();

            Book book2 = Book.builder()
                    .title("The Pragmatic Programmer")
                    .isbn("9780201616224")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Andy Hunt")
                                            .url("http://openlibrary.org/authors/OL1391034A/Andy_Hunt")
                                            .build(),
                                    Author.builder()
                                            .name("Dave Thomas")
                                            .url("http://openlibrary.org/authors/OL1439324A/Dave_Thomas")
                                            .build()
                            )
                    )
                    .subtitle("From Journeyman to Master")
                    .publishers(List.of(
                            Publisher.builder()
                                    .name("Addison-Wesley")
                                    .build()
                            )
                    )
                    .publishedDate("1999")
                    .imageUrl("https://covers.openlibrary.org/b/id/7386133-L.jpg")
                    .build();

            Book book3 = Book.builder()
                    .title("Fantastic Mr. Fox")
                    .isbn("9780140328721")
                    .authors(
                            List.of(Author
                            .builder()
                            .name("Roald Dahl")
                            .url("http://openlibrary.org/authors/OL34184A/Roald_Dahl")
                            .build()
                            )
                    )
                    .publishers(List.of(
                            Publisher.builder()
                                    .name("Puffin")
                                    .build()
                            )
                    )
                    .publishedDate("October 1, 1988")
                    .imageUrl("https://covers.openlibrary.org/b/id/15152634-L.jpg")
                    .build();

            bookRepository.saveAll(List.of(book1, book2, book3));
            log.info("Sample books initialized.");
        }
    }
}

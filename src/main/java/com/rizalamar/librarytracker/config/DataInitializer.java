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
    public void run(String... args) {
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

            Book book4 = Book.builder()
                    .title("The Great Gatsby")
                    .isbn("9780743273565")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("F. Scott Fitzgerald")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Independently Published")
                                            .build()
                            )
                    )
                    .publishedDate("2021")
                    .imageUrl("https://covers.openlibrary.org/b/id/14314120-L.jpg")
                    .available(true)
                    .build();

            Book book5 = Book.builder()
                    .title("1984")
                    .isbn("9780451524935")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("George Orwell")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Signet Classic")
                                            .build()
                            )
                    )
                    .publishedDate("1993")
                    .imageUrl("https://covers.openlibrary.org/b/id/12054527-L.jpg")
                    .available(true)
                    .build();

            Book book6 = Book.builder()
                    .title("The Hobbit")
                    .isbn("9780547928227")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("J.R.R. Tolkien")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Mariner Books")
                                            .build()
                            )
                    )
                    .publishedDate("2012")
                    .imageUrl("https://covers.openlibrary.org/b/id/12003329-L.jpg")
                    .available(true)
                    .build();

            Book book7 = Book.builder()
                    .title("Killing Floor")
                    .isbn("9780515141429")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Lee Child")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Jove Books")
                                            .build()
                            )
                    )
                    .publishedDate("2018")
                    .imageUrl("https://covers.openlibrary.org/b/id/14424676-L.jpg")
                    .available(true)
                    .build();

            Book book8 = Book.builder()
                    .title("Thinking, Fast and Slow")
                    .isbn("9780374533557")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Daniel Kahneman")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Farrar, Straus and Giroux").build()
                            )
                    )
                    .publishedDate("April 2, 2013")
                    .imageUrl("https://covers.openlibrary.org/b/id/7889800-L.jpg")
                    .available(true)
                    .build();

            Book book9 = Book.builder()
                    .title("Educated")
                    .isbn("9780399590504")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Sarah Fields")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(Publisher.builder()
                                    .name("Blurb")
                                    .build()
                            )
                    )
                    .publishedDate("2018")
                    .imageUrl("https://covers.openlibrary.org/b/id/14832082-L.jpg")
                    .available(true)
                    .build();

            Book book10 = Book.builder()
                    .title("The Alchemist")
                    .isbn("9780062315007")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Paulo Coelho")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("HarperCollins Publishers")
                                            .build()
                            )
                    )
                    .publishedDate("2014")
                    .imageUrl("https://covers.openlibrary.org/b/id/15091614-L.jpg")
                    .available(true)
                    .build();

            Book book11 = Book.builder()
                    .title("Sapiens")
                    .isbn("9780062316097")
                    .authors(
                            List.of(
                                    Author.builder()
                                            .name("Yuval Noah Harari")
                                            .build()
                            )
                    )
                    .publishers(
                            List.of(
                                    Publisher.builder()
                                            .name("Harper")
                                            .build()
                            )
                    )
                    .publishedDate("2011")
                    .imageUrl("https://covers.openlibrary.org/b/id/14369194-L.jpg")
                    .available(true)
                    .build();

            bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11));
            log.info("Sample books initialized.");
        }
    }
}

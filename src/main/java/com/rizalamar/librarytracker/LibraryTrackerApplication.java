package com.rizalamar.librarytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LibraryTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryTrackerApplication.class, args);
    }

}

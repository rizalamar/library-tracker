package com.rizalamar.librarytracker.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "books")
public class Book extends AbstractAuditingEntity {

    @Column(nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "book_id")
    private Set<Author> authors;

    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "book_id")
    private Set<Publisher> publishers;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    private boolean available = true;
}

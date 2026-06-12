package com.rizalamar.librarytracker.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
    private List<Author> authors;

    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "book_id")
    private List<Publisher> publishers;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    private boolean available = true;
}

package com.rizalamar.librarytracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(nullable = false)
    private String author;

    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String publisher;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    private boolean available = true;
}

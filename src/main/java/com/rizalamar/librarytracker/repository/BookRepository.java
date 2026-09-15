package com.rizalamar.librarytracker.repository;

import com.rizalamar.librarytracker.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    @Override
    @EntityGraph(attributePaths = {"authors", "publishers"})
    List<Book> findAll();
}

package com.rizalamar.librarytracker.repository;

import com.rizalamar.librarytracker.domain.MyBook;
import com.rizalamar.librarytracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MyBookRepository extends JpaRepository<MyBook, UUID> {
    List<MyBook> findAllByUser (User user);

    boolean existsByUserAndBookId(User user, UUID bookId);

    void deleteByUserAndId(User user, UUID myBookId);
}

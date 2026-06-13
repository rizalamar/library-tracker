package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.Book;
import com.rizalamar.librarytracker.domain.MyBook;
import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.mybook.MyBookResponse;
import com.rizalamar.librarytracker.repository.BookRepository;
import com.rizalamar.librarytracker.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyBookService {
    private final MyBookRepository myBookRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<MyBookResponse> getMyBooks(User user){
        return myBookRepository.findAllByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MyBookResponse addBookToCollection(User user, UUID bookId){
        boolean isBookExists = myBookRepository.existsByUserAndBookId(user, bookId);

        if(isBookExists){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book already in your collection");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        MyBook myBook = MyBook.builder()
                .user(user)
                .book(book)
                .build();

        MyBook savedMyBook = myBookRepository.save(myBook);

        return mapToResponse(savedMyBook);
    }

    @Transactional
    public void removeBookFromCollection(User user, UUID myBookId){
        myBookRepository.deleteByUserAndId(user, myBookId);
    }

    private MyBookResponse mapToResponse(MyBook myBook){
        Book book = myBook.getBook();

        BookResponse bookResponse = BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authors(
                        book.getAuthors() != null ?
                                book.getAuthors().stream().map(
                                        author -> new BookResponse.Author(author.getUrl(), author.getName())
                                ).toList()
                                : List.of()
                )
                .isbn(book.getIsbn())
                .subtitle(book.getSubtitle())
                .publishers(
                        book.getPublishers() != null ?
                                book.getPublishers().stream().map(
                                        publisher -> new BookResponse.Publishers(publisher.getName())
                                ).toList()
                                : List.of()
                )
                .publishedDate(book.getPublishedDate())
                .imageUrl(book.getImageUrl())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .build();

        return MyBookResponse.builder()
                .id(myBook.getId())
                .book(bookResponse)
                .createdAt(myBook.getCreatedAt())
                .build();
    }

}

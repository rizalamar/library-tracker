package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.Book;
import com.rizalamar.librarytracker.domain.MyBook;
import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.dto.book.BookResponse;
import com.rizalamar.librarytracker.dto.mybook.MyBookRequest;
import com.rizalamar.librarytracker.dto.mybook.MyBookResponse;
import com.rizalamar.librarytracker.repository.BookRepository;
import com.rizalamar.librarytracker.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyBookService {
    private final MyBookRepository myBookRepository;
    private final BookRepository bookRepository;
    private final ValidationService validationService;
    private final OpenLibraryService openLibraryService;

    @Transactional(readOnly = true)
    public List<MyBookResponse> getMyBooks(User user){
        return myBookRepository.findAllByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MyBookResponse addBookToCollection(User user, UUID bookId){
       validationService.validateBookNotAlreadyInCollection(user, bookId);

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
    public MyBookResponse updateMyBook(User user, UUID myBookId, MyBookRequest request){
        MyBook myBook = myBookRepository.findById(myBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MyBook entry not found"));

        validationService.validateMyBookOwnerShip(user, myBook);

        if(Objects.nonNull(request.status())){
            myBook.setStatus(request.status());
        }

        if(Objects.nonNull(request.notes())){
            myBook.setNotes(request.notes());
        }

        MyBook updatedBook = myBookRepository.save(myBook);

        return mapToResponse(updatedBook);
    }

    @Transactional
    public void removeBookFromCollection(User user, UUID myBookId){
        myBookRepository.deleteByUserAndId(user, myBookId);
    }

    private MyBookResponse mapToResponse(MyBook myBook){
        Book baseBook = myBook.getBook();

        BookResponse enrichedBook = null;

        try{
            enrichedBook = openLibraryService.fetchBookByIsbn(baseBook.getIsbn());
        } catch (Exception ignored) {

        }

        BookResponse bookResponse = mapToBookResponse(baseBook, enrichedBook);

        return MyBookResponse.builder()
                .id(myBook.getId())
                .book(bookResponse)
                .status(myBook.getStatus())
                .notes(myBook.getNotes())
                .createdAt(myBook.getCreatedAt())
                .build();
    }

    private BookResponse mapToBookResponse(Book book, BookResponse enriched){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .subtitle(book.getSubtitle())
                .authors(
                        book.getAuthors() != null ?
                                book.getAuthors().stream()
                                        .map(
                                                author -> new BookResponse.Author(
                                                        author.getName(),
                                                        author.getUrl()
                                                )
                                        ).toList() : List.of())
                .publishers(
                        book.getPublishers() != null ?
                                book.getPublishers().stream()
                                        .map(
                                                publisher -> new BookResponse.Publishers(
                                                        publisher.getName()
                                                )
                                        ).toList() : List.of()
                )
                .number_of_pages(enriched != null ? enriched.number_of_pages() : null)
                .subjects(enriched != null ? enriched.subjects() : List.of())
                .subjectsPeople(enriched != null ? enriched.subjectsPeople() : List.of())
                .subjectPlaces(enriched != null ? enriched.subjectPlaces() : List.of())
                .subjectTimes(enriched != null ? enriched.subjectTimes() : List.of())
                .excerpts(enriched != null ? enriched.excerpts() : List.of())
                .publishedDate(book.getPublishedDate())
                .imageUrl(book.getImageUrl())
                .available(book.isAvailable())
                .createdAt(book.getCreatedAt())
                .build();
    }

}

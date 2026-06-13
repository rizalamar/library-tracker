package com.rizalamar.librarytracker.service;

import com.rizalamar.librarytracker.domain.MyBook;
import com.rizalamar.librarytracker.domain.User;
import com.rizalamar.librarytracker.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final MyBookRepository myBookRepository;

    public void validateBookNotAlreadyInCollection(User user, UUID bookId){
        if(myBookRepository.existsByUserAndBookId(user, bookId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book already in your collection");
        }
    }

    public void validateMyBookOwnerShip(User user, MyBook myBook){
        if(!myBook.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this book");
        }
    }
}

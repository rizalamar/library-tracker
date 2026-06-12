package com.rizalamar.librarytracker.exception;

import com.rizalamar.librarytracker.dto.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> handleResponseStatusException(ResponseStatusException exception){
        return ResponseEntity.status(exception.getStatusCode())
                .body(
                        WebResponse.<String>builder()
                                .code(exception.getStatusCode().value())
                                .status(exception.getStatusCode().toString())
                                .data(exception.getReason())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<Map<String, String>>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .status("BAD REQUEST")
                                .data(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleGeneralException(Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        WebResponse.<String>builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .status("INTERNAL SERVER ERRIR")
                                .data("Error occured: " + exception.getMessage())
                                .build()
                );
    }
}

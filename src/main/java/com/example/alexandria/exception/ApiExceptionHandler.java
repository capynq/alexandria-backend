package com.example.alexandria.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason() == null ? "Request failed" : ex.getReason()));
    }
    @ExceptionHandler({IllegalArgumentException.class, DateTimeParseException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, String>> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
    }
    @ExceptionHandler({NoSuchElementException.class, UserNotFoundException.class})
    ResponseEntity<Map<String, String>> notFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Resource not found"));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, String>> conflict(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Request conflicts with existing data"));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> internal(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
    }
}

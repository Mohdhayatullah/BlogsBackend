package org.blogs.Blogs.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse(exception.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InstantiationException.class)
    public ResponseEntity<?> handleException(InstantiationException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleException(JwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("File too large! Max allowed is 10MB.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleException(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("Duplicate or invalid data", HttpStatus.BAD_REQUEST));
    }

    private Map<String, Object> errorResponse(String message, HttpStatus status) {
        return Map.of(
                "message", message,
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "timeStamps", LocalDateTime.now().toString()
        );
    }
}

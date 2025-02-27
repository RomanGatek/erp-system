package cz.syntaxbro.erpsystem.exceptions;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Input data validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Set<ErrorEntity>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Set<ErrorEntity> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldError = ((FieldError) error).getField();
            var entity = new ErrorEntity(fieldError, error.getDefaultMessage());
            errors.add(entity);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Error when user not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Error with unauthorized access (e.g. wrong password)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + ex.getMessage());
    }

    // Error when entering incorrect information (e.g. username already exists)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorEntity> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErpSystemApplication.getLogger().log(Level.WARNING, ex.getMessage());
        var entity = new ErrorEntity("email", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(entity);
    }

    // General server error (unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEntity> handleGlobalException(Exception ex) {
        ErpSystemApplication.getLogger().log(Level.WARNING, ex.getMessage());
        ErpSystemApplication.getLogger().log(Level.WARNING, ex.getClass().getName());
        var entity = new ErrorEntity("password", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(entity);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorEntity> handleResourceNotFoundException(NoResourceFoundException ex) {
        ErpSystemApplication.getLogger().log(Level.WARNING, ex.getMessage());
        var entity = new ErrorEntity("resource", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity);
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorEntity {
        private String field;
        private String message;
    }
}
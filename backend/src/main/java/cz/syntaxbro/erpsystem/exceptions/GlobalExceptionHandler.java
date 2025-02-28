package cz.syntaxbro.erpsystem.exceptions;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashSet;
import java.util.Set;

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
    public ResponseEntity<ErrorEntity> handleUserNotFoundException(UserNotFoundException ex) {
        var entity = new ErrorEntity("user", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity);
    }

    // Error with unauthorized access (e.g. wrong password)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorEntity> handleAccessDeniedException(AccessDeniedException ex) {
        var entity = new ErrorEntity("access", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(entity);
    }

    // Error when entering incorrect information (e.g. username already exists)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorEntity> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        var entity = new ErrorEntity("argument", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(entity);
    }

    // General server error (unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEntity> handleGlobalException(Exception ex) {
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        ErpSystemApplication.getLogger().warn(ex.getClass().getName());
        var entity = new ErrorEntity("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(entity);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorEntity> handleResourceNotFoundException(NoResourceFoundException ex) {
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        var entity = new ErrorEntity("resource", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorEntity> handleDuplicateEntry(DataIntegrityViolationException ex) {
        var entity = new ErrorEntity("database", ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            entity.setMessage("Item with this name already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(entity);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(entity);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleEntityNotFound(EntityNotFoundException ex) {
        var entity = new ErrorEntity("entity", ex.getMessage());
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorEntity> handleValidationException(ConstraintViolationException ex) {
        var entity = new ErrorEntity("validation", ex.getMessage());
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(entity);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorEntity> handleResponseStatusException(ResponseStatusException ex) {
        var entity = new ErrorEntity("response", ex.getReason());
        ErpSystemApplication.getLogger().warn(ex.getMessage());
        return new ResponseEntity<>(entity, ex.getStatusCode());
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
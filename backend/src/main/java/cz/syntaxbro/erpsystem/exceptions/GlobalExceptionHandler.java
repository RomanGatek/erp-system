package cz.syntaxbro.erpsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicateEntry(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Item with this name already exists.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("A database error occurred.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> error(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }


}
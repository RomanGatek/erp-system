package cz.syntaxbro.erpsystem.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.assertj.core.api.Assertions.assertThat;

@ControllerAdvice
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFoundException_shouldReturnNotFound() {
        GlobalExceptionHandler.UserNotFoundException exception = new GlobalExceptionHandler.UserNotFoundException("User not found");
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleUserNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleAccessDeniedException_shouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleAccessDeniedException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    // Add more tests for other exception handlers...
} 
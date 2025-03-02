package cz.syntaxbro.erpsystem.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    /**
     * Test: Should return HTTP 404 NOT FOUND for UserNotFoundException.
     * Expected Outcome:
     * - HTTP 404 Status Code
     * - The error message should match "User not found"
     */
    @Test
    void handleUserNotFoundException_shouldReturnNotFound() {
        // Arrange
        GlobalExceptionHandler.UserNotFoundException exception = new GlobalExceptionHandler.UserNotFoundException("User not found");

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleUserNotFoundException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    /**
     * Test: Should return HTTP 403 FORBIDDEN for AccessDeniedException.
     * Expected Outcome:
     * - HTTP 403 Status Code
     * - The error message should match "Access denied"
     */
    @Test
    void handleAccessDeniedException_shouldReturnForbidden() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    /**
     * Test: Should return HTTP 400 BAD REQUEST for IllegalArgumentException.
     * Expected Outcome:
     * - HTTP 400 Status Code
     * - The error message should match the input exception message
     */
    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input data");

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input data");
    }

    /**
     * Test: Should return HTTP 500 INTERNAL SERVER ERROR for generic Exception.
     * Expected Outcome:
     * - HTTP 500 Status Code
     * - The error message should match the input exception message
     */
    @Test
    void handleGlobalException_shouldReturnInternalServerError() {
        // Arrange
        Exception exception = new Exception("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleGlobalException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Unexpected error occurred");
    }

    /**
     * Test: Should return HTTP 409 CONFLICT when DataIntegrityViolationException contains "Duplicate entry".
     * Expected Outcome:
     * - HTTP 409 Conflict Status Code
     * - The message should indicate that a duplicate entry exists.
     */
    @Test
    void handleDuplicateEntry_shouldReturnConflictForDuplicateEntry() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Duplicate entry for key 'email'");

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleDuplicateEntry(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Item with this name already exists.");
    }

    /**
     * Test: Should return HTTP 404 NOT FOUND for NoResourceFoundException.
     * Expected Outcome:
     * - HTTP 404 Status Code
     * - The error message should match the exception message.
     */
    @Test
    void handleResourceNotFoundException_shouldReturnNotFound() {
        // Arrange
        String testPath = "/non-existent-page";
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, testPath);

        // Act
        ResponseEntity<ErrorEntity> response = globalExceptionHandler.handleResourceNotFoundException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("No static resource " + testPath + ".");
    }
}
package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the LoginRequest class.
 * These tests ensure that validation constraints work correctly.
 */
class LoginRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate LoginRequest objects.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted login request.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidData() {
        // Arrange: Create a valid login request
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "SecurePass123!"
        );

        // Act: Validate the request
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert: No validation errors should be present
        assertThat(violations).isEmpty();
    }

    /**
     * Test case to check validation failure when email is blank.
     * Expected: Validation error for missing email.
     */
    @Test
    void shouldFailValidationForBlankEmail() {
        // Arrange: Create a request with missing email
        LoginRequest request = new LoginRequest(
                "",
                "SecurePass123!"
        );

        // Act: Validate the request
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert: Email validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Email is required"));
    }

    /**
     * Test case to check validation failure when password is blank.
     * Expected: Validation error for missing password.
     */
    @Test
    void shouldFailValidationForBlankPassword() {
        // Arrange: Create a request with missing password
        LoginRequest request = new LoginRequest(
                "user@example.com",
                ""
        );

        // Act: Validate the request
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password is required"));
    }

    /**
     * Test case to check validation failure when both email and password are blank.
     * Expected: Validation errors for both missing email and password.
     */
    @Test
    void shouldFailValidationForBlankEmailAndPassword() {
        // Arrange: Create a request with missing email and password
        LoginRequest request = new LoginRequest(
                "",
                ""
        );

        // Act: Validate the request
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert: Validation should fail for both fields
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "Email is required",
                        "Password is required"
                );
    }
}
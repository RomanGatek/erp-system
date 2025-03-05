package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the SignUpRequest class.
 * These tests ensure that validation constraints work correctly.
 */
class SignUpRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate SignUpRequest objects.
     */
    @BeforeAll
    static void setUp() {
        // Set default locale to English to ensure validation messages are in English
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted sign-up request.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidData() {
        // Arrange: Create a valid sign-up request
        SignUpRequest request = new SignUpRequest(
                "ValidUser123",    // Valid username
                "SecurePass123!",  // Valid password
                "valid@example.com" // Valid email
        );

        // Act: Validate the request
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Assert: No validation errors should be present
        assertThat(violations).isEmpty();
    }

    /**
     * Test case to check validation failure when username is too short.
     * Expected: Validation error for username length.
     */
    @Test
    void shouldFailValidationForShortUsername() {
        // Arrange: Create a sign-up request with a short username
        SignUpRequest request = new SignUpRequest(
                "usr", // Too short (less than 5 characters)
                "SecurePass123!",
                "valid@example.com"
        );

        // Act: Validate the request
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Assert: Username validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Username muset be between 5 and 50 characters"));
    }

    /**
     * Test case to check validation failure when password is too short.
     * Expected: Validation error for password length.
     */
    @Test
    void shouldFailValidationForShortPassword() {
        // Arrange: Create a sign-up request with a short password
        SignUpRequest request = new SignUpRequest(
                "ValidUser123",
                "short", // Too short (less than 10 characters)
                "valid@example.com"
        );

        // Act: Validate the request
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password must be at least 10 characters"));
    }

    /**
     * Test case to check validation failure when email format is invalid.
     * Expected: Validation error for email format.
     */
    @Test
    void shouldFailValidationForInvalidEmail() {
        // Arrange: Create a sign-up request with an invalid email
        SignUpRequest request = new SignUpRequest(
                "ValidUser123",
                "SecurePass123!",
                "invalid-email" // Invalid email format
        );

        // Act: Validate the request
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Assert: Email validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    /**
     * Test case to check validation failure when fields are blank.
     * Expected: Validation errors for required fields.
     */
    @Test
    void shouldFailValidationForBlankFields() {
        // Arrange: Create a sign-up request with blank fields
        SignUpRequest request = new SignUpRequest(
                "", // Blank username
                "", // Blank password
                ""  // Blank email
        );

        // Act: Validate the request
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Assert: Multiple validation errors should be present
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsAnyOf(
                        "Username is required",
                        "Password is required",
                        "Invalid email format",
                        "must not be blank"
                );
}
}
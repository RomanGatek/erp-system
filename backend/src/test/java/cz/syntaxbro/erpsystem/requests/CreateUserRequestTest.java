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
 * Unit tests for the CreateUserRequest class.
 * These tests ensure that validation constraints work correctly.
 */
class CreateUserRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate CreateUserRequest objects.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted user request.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidData() {
        // Arrange: Create a valid user request
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "StrongPass1!",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert: No validation errors should be present
        assertThat(violations).isEmpty();
    }

    /**
     * Test case to check validation failure for an invalid username.
     * Expected: Validation error for username length constraint.
     */
    @Test
    void shouldFailValidationForInvalidUsername() {
        // Arrange: Create a request with an invalid short username
        CreateUserRequest request = new CreateUserRequest(
                "us", // Too short
                "StrongPass1!",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert: Username validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Username must be between 5 and 50 characters"));
    }

    /**
     * Test case to check validation failure for an invalid password.
     * Expected: Validation error for password strength constraints.
     */
    @Test
    void shouldFailValidationForInvalidPassword() {
        // Arrange: Create a request with a weak password
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "weakpass", // Does not meet strength requirements
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    /**
     * Test case to check validation failure for an invalid email format.
     * Expected: Validation error for email format.
     */
    @Test
    void shouldFailValidationForInvalidEmail() {
        // Arrange: Create a request with an incorrectly formatted email
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "StrongPass1!",
                "invalid-email", // Not a valid email format
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert: Email validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    /**
     * Test case to check validation failure when username and password are blank.
     * Expected: Validation errors for both missing username and password.
     */
    @Test
    void shouldFailValidationForBlankUsernameAndPassword() {
        // Arrange: Create a request with missing username and password
        CreateUserRequest request = new CreateUserRequest(
                "",
                "",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        // Assert: Validation should fail for both username and password
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "Username is required",
                        "Password is required"
                );
    }
}
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
 * Unit tests for the PasswordChangeRequest class.
 * Ensures that validation constraints are properly enforced.
 */
class PasswordChangeRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate PasswordChangeRequest objects.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted password.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidPassword() {
        // Arrange: Create a valid password change request
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("SecurePass1!");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: No validation errors should be present
        assertThat(violations).isEmpty();
    }

    /**
     * Test case to check validation failure when password is blank.
     * Expected: Validation error for missing password.
     */
    @Test
    void shouldFailValidationForBlankPassword() {
        // Arrange: Create a password change request with an empty password
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password is required"));
    }

    /**
     * Test case to check validation failure when password is too short.
     * Expected: Validation error for short password.
     */
    @Test
    void shouldFailValidationForShortPassword() {
        // Arrange: Create a password change request with a short password
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("Short1!");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must be at least 8 characters"));
    }

    /**
     * Test case to check validation failure when password does not meet complexity requirements.
     * Expected: Validation error for missing uppercase letter, digit, or special character.
     */
    @Test
    void shouldFailValidationForMissingUppercaseLetter() {
        // Arrange: Create a password change request without an uppercase letter
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("weakpassword1!");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    /**
     * Test case to check validation failure when password does not include a digit.
     * Expected: Validation error for missing digit.
     */
    @Test
    void shouldFailValidationForMissingNumber() {
        // Arrange: Create a password change request without a number
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("NoNumbersHere!");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"));
    }

    /**
     * Test case to check validation failure when password does not include a special character.
     * Expected: Validation error for missing special character.
     */
    @Test
    void shouldFailValidationForMissingSpecialCharacter() {
        // Arrange: Create a password change request without a special character
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setPassword("Password123");

        // Act: Validate the request
        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);

        // Assert: Password validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"));
    }
}
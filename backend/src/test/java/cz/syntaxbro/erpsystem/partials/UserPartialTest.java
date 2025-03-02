package cz.syntaxbro.erpsystem.partials;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserPartial DTO.
 * Ensures that validation constraints are correctly applied when updating user information.
 */
class UserPartialTest {

    private Validator validator;

    /**
     * Initializes the validator before each test.
     * The validator will check if the constraints defined in the UserPartial class are working correctly.
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test to verify that a valid UserPartial object does not produce any validation errors.
     */
    @Test
    void shouldCreateValidUserPartial() {
        // Arrange: Create a valid UserPartial object
        UserPartial userPartial = new UserPartial(
                "ValidUser_123",
                "user@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
    }

    /**
     * Test to verify that a username shorter than 5 characters fails validation.
     */
    @Test
    void shouldFailValidation_WhenUsernameIsTooShort() {
        // Arrange: Create a UserPartial object with a short username
        UserPartial userPartial = new UserPartial(
                "usr",
                "user@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: Expect at least one validation error related to username length
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("must be between 5 and 50 characters"))).isTrue();
    }

    /**
     * Test to verify that a username longer than 50 characters fails validation.
     */
    @Test
    void shouldFailValidation_WhenUsernameIsTooLong() {
        // Arrange: Create a UserPartial object with an excessively long username
        UserPartial userPartial = new UserPartial(
                "a".repeat(51), // 51-character username
                "user@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: Expect a validation error due to excessive username length
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("must be between 5 and 50 characters"))).isTrue();
    }

    /**
     * Test to verify that a username containing invalid characters fails validation.
     */
    @Test
    void shouldFailValidation_WhenUsernameContainsInvalidCharacters() {
        // Arrange: Create a UserPartial object with an invalid username
        UserPartial userPartial = new UserPartial(
                "Invalid@Name!",
                "user@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: Expect a validation error due to invalid username format
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("can only contain letters, numbers, and underscores"))).isTrue();
    }

    /**
     * Test to verify that an invalid email format fails validation.
     */
    @Test
    void shouldFailValidation_WhenEmailIsInvalid() {
        // Arrange: Create a UserPartial object with an invalid email
        UserPartial userPartial = new UserPartial(
                "ValidUser",
                "invalid-email",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: Expect a validation error related to invalid email format
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format"))).isTrue();
    }

    /**
     * Test to verify that an empty email field passes validation (email is optional).
     */
    @Test
    void shouldPassValidation_WhenEmailIsNull() {
        // Arrange: Create a UserPartial object with no email
        UserPartial userPartial = new UserPartial(
                "ValidUser",
                null, // Email is optional
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: No validation errors should occur, as email is optional
        assertThat(violations).isEmpty();
    }

    /**
     * Test to ensure that a UserPartial object can be created with an empty role set.
     */
    @Test
    void shouldAllowEmptyRoles() {
        // Arrange: Create a UserPartial object with an empty role set
        UserPartial userPartial = new UserPartial(
                "ValidUser",
                "user@example.com",
                "John",
                "Doe",
                true,
                Set.of() // No roles assigned
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userPartial);

        // Assert: No validation errors should occur
        assertThat(violations).isEmpty();
        assertThat(userPartial.getRoles()).isEmpty();
    }
}
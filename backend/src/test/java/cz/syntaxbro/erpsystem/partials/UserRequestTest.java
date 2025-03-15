package cz.syntaxbro.erpsystem.partials;

import cz.syntaxbro.erpsystem.requests.UserRequest;
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
class UserRequestTest {

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
        UserRequest userRequest = new UserRequest(
                "ValidUser_123",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER"),
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
    }

    /**
     * Test to verify that a username shorter than 5 characters fails validation.
     */
    @Test
    void shouldFailValidation_WhenUsernameIsTooShort() {
        // Arrange: Create a UserPartial object with a short username
        UserRequest userRequest = new UserRequest(
                "usr",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER"),
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

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
        UserRequest userRequest = new UserRequest(
                "a".repeat(51), // 51-character username
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER"),
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

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
        UserRequest userRequest = new UserRequest(
                "Invalid@Name!",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER"),
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

        // Assert: Expect a validation error due to invalid username format
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("can only contain letters, numbers, and underscores"))).isTrue();
    }


    /**
     * Test to verify that an empty email field passes validation (email is optional).
     */
    @Test
    void shouldPassValidation_WhenEmailIsNull() {
        // Arrange: Create a UserPartial object with no email
        UserRequest userRequest = new UserRequest(
                "ValidUser",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER"),
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

        // Assert: No validation errors should occur, as email is optional
        assertThat(violations).isEmpty();
    }

    /**
     * Test to ensure that a UserPartial object can be created with an empty role set.
     */
    @Test
    void shouldAllowEmptyRoles() {
        // Arrange: Create a UserPartial object with an empty role set
        UserRequest userRequest = new UserRequest(
                "ValidUser",
                "John",
                "Doe",
                true,
                Set.of(), // No roles assigned,
                null
        );

        // Act: Validate the userPartial object
        var violations = validator.validate(userRequest);

        // Assert: No validation errors should occur
        assertThat(violations).isEmpty();
        assertThat(userRequest.getRoles()).isEmpty();
    }
}
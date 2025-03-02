package cz.syntaxbro.erpsystem.models;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;

/**
 * Unit test for the User entity.
 * Ensures that validation constraints are correctly applied and the User entity behaves as expected.
 */
class UserTest {

    private Validator validator;

    /**
     * Initializes the validator before each test execution.
     * The validator is used to verify if validation constraints on User attributes are working as expected.
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test to verify that a valid User entity is created without validation errors.
     */
    @Test
    void shouldCreateValidUser() {
        // Arrange: Create a valid User object
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .password("SecurePassword123!")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .avatar("avatar.jpg")
                .active(true)
                .roles(Set.of(new Role("ROLE_USER")))
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getPassword()).isEqualTo("SecurePassword123!");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getAvatar()).isEqualTo("avatar.jpg");
        assertThat(user.isActive()).isTrue();
        assertThat(user.getRoles()).hasSize(1);
    }

    /**
     * Test to ensure that a User entity without a username fails validation.
     */
    @Test
    void shouldFailValidation_WhenUsernameIsMissing() {
        // Arrange: Create a User object without a username
        User user = User.builder()
                .password("SecurePassword123!")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there is at least one validation error
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required"))).isTrue();
    }

    /**
     * Test to ensure that a User entity without a valid email fails validation.
     */
    @Test
    void shouldFailValidation_WhenEmailIsInvalid() {
        // Arrange: Create a User object with an invalid email format
        User user = User.builder()
                .username("testUser")
                .password("SecurePassword123!")
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email")
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there is at least one validation error for the email
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().equals("Email should be valid"))).isTrue();
    }

    /**
     * Test to ensure that a User entity without a password fails validation.
     */
    @Test
    void shouldFailValidation_WhenPasswordIsMissing() {
        // Arrange: Create a User object without a password
        User user = User.builder()
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there is at least one validation error for the password
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required"))).isTrue();
    }

    /**
     * Test to ensure that a User entity without a first name fails validation.
     */
    @Test
    void shouldFailValidation_WhenFirstNameIsMissing() {
        // Arrange: Create a User object without a first name
        User user = User.builder()
                .username("testUser")
                .password("SecurePassword123!")
                .lastName("Doe")
                .email("test@example.com")
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there is at least one validation error for the first name
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().equals("First name is required"))).isTrue();
    }

    /**
     * Test to ensure that a User entity without a last name fails validation.
     */
    @Test
    void shouldFailValidation_WhenLastNameIsMissing() {
        // Arrange: Create a User object without a last name
        User user = User.builder()
                .username("testUser")
                .password("SecurePassword123!")
                .firstName("John")
                .email("test@example.com")
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there is at least one validation error for the last name
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().equals("Last name is required"))).isTrue();
    }

    /**
     * Test to ensure that a User entity can be created with an empty role set.
     */
    @Test
    void shouldAllowEmptyRoles() {
        // Arrange: Create a User object with an empty role set
        User user = User.builder()
                .username("testUser")
                .password("SecurePassword123!")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .roles(Set.of()) // Empty roles
                .build();

        // Act: Validate the user
        var violations = validator.validate(user);

        // Assert: Ensure that there are no validation errors
        assertThat(violations).isEmpty();
        assertThat(user.getRoles()).isEmpty();
    }
}
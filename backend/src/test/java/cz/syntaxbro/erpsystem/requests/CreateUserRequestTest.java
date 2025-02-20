package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithValidData() {
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "StrongPass1!",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationForInvalidUsername() {
        CreateUserRequest request = new CreateUserRequest(
                "us",
                "StrongPass1!",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Username must be between 5 and 50 characters"));
    }

    @Test
    void shouldFailValidationForInvalidPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "weakpass",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    @Test
    void shouldFailValidationForInvalidEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "ValidUser123",
                "StrongPass1!",
                "invalid-email",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    void shouldFailValidationForBlankUsernameAndPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "",
                "",
                "valid@example.com",
                "John",
                "Doe",
                true,
                Set.of("ROLE_USER")
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "Username is required",
                        "Password is required"
                );
    }

}

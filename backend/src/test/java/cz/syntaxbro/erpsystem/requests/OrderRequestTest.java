package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the OrderRequest class.
 * These tests ensure that validation constraints work correctly.
 */
class OrderRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate OrderRequest objects.
     */
    @BeforeAll
    static void setUp() {
        Locale.setDefault(Locale.ENGLISH); // Ensures messages are in English
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted order request.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidData() {
        // Arrange: Create a valid order request
        OrderRequest request = new OrderRequest(
                5, // amount
                150.0, // cost
                Order.Status.ORDERED, // status
                LocalDateTime.now().plusDays(1), // order time in the future
                1L // valid product ID
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: No validation errors should be present
        assertThat(violations).isEmpty();
    }

    /**
     * Test case to check validation failure when cost is null.
     * Expected: Validation error for missing cost.
     */
    @Test
    void shouldFailValidationForNullCost() {
        // Arrange: Create an order request with null cost
        OrderRequest request = new OrderRequest(
                5,
                null,
                Order.Status.ORDERED,
                LocalDateTime.now().plusDays(1),
                1L
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Cost validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Cost cannot be null"));
    }

    /**
     * Test case to check validation failure when cost is negative.
     * Expected: Validation error for negative cost.
     */
    @Test
    void shouldFailValidationForNegativeCost() {
        // Arrange: Create an order request with negative cost
        OrderRequest request = new OrderRequest(
                5,
                -10.0,
                Order.Status.ORDERED,
                LocalDateTime.now().plusDays(1),
                1L
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Cost validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Cost must be greater than 0"));
    }

    /**
     * Test case to check validation failure when status is null.
     * Expected: Validation error for missing status.
     */
    @Test
    void shouldFailValidationForNullStatus() {
        // Arrange: Create an order request with null status
        OrderRequest request = new OrderRequest(
                5,
                150.0,
                null,
                LocalDateTime.now().plusDays(1),
                1L
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Status validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Status cannot be null"));
    }

    /**
     * Test case to check validation failure when order time is in the past.
     * Expected: Validation error for order time.
     */
    @Test
    void shouldFailValidationForPastOrderTime() {
        // Arrange: Create an order request with past order time
        OrderRequest request = new OrderRequest(
                5,
                150.0,
                Order.Status.ORDERED,
                LocalDateTime.now().minusDays(1), // past date
                1L
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Order time validation should fail (localized message fix)
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("must be a date in the present or in the future"));
    }

    /**
     * Test case to check validation failure when product ID is null.
     * Expected: Validation error for missing product ID.
     */
    @Test
    void shouldFailValidationForNullProductId() {
        // Arrange: Create an order request with null product ID
        OrderRequest request = new OrderRequest(
                5,
                150.0,
                Order.Status.ORDERED,
                LocalDateTime.now().plusDays(1),
                null
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Product ID validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Status cannot be null"));
    }

    /**
     * Test case to check validation failure when product ID is less than 1.
     * Expected: Validation error for product ID.
     */
    @Test
    void shouldFailValidationForInvalidProductId() {
        // Arrange: Create an order request with invalid product ID
        OrderRequest request = new OrderRequest(
                5,
                150.0,
                Order.Status.ORDERED,
                LocalDateTime.now().plusDays(1),
                0L
        );

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Product ID validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Product ID must be greater than 0"));
    }
}
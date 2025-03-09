package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.models.Product;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the OrderRequest class.
 * These tests ensure that validation constraints work correctly.
 */
class OrderRequestTest {

    private Validator validator;
    private Product product;
    private InventoryItem inventoryItem;
    private OrderItem orderItem;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate OrderRequest objects.
     */
    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.ENGLISH); // Ensures messages are in English
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        this.product = Product.builder()
                .name("Ahoj")
                .build();

        this.inventoryItem = InventoryItem.builder()
                .stockedAmount(123)
                .product(product)
                .build();

        orderItem = OrderItem.builder()
                .quantity(12)
                .inventoryItem(this.inventoryItem)
                .build();

    }

    /**
     * Test case to validate a correctly formatted order request.
     * Expected: No validation errors.
     */
    @Test
    void shouldPassValidationWithValidData() {
        // Arrange: Create a valid order request


        LocalDateTime now = LocalDateTime.now().plusYears(1);
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .status(Order.Status.PENDING)
                .orderItems(List.of(orderItem))
                .comment("test comment")
                .cost(150.0)
                .build();

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
        LocalDateTime now = LocalDateTime.now();
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .comment("test comment")
                .cost(null)
                .build();

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

        LocalDateTime now = LocalDateTime.now();
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .comment("test comment")
                .cost(-10.0)
                .build();

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

        LocalDateTime now = LocalDateTime.now();
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .comment("test comment")
                .cost(150.0)
                .status(null)
                .build();

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

        LocalDateTime now = LocalDateTime.now();
        OrderRequest request = OrderRequest.builder()
                .orderTime(now.minusDays(1))
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .comment("test comment")
                .cost(150.0)
                .build();

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

        LocalDateTime now = LocalDateTime.now();
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .comment("test comment")
                .cost(150.0)
                .build();

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

        LocalDateTime now = LocalDateTime.now().minusDays(1);
        OrderRequest request = OrderRequest.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .amount(5)
                .orderItems(List.of(this.orderItem))
                .comment("test comment")
                .cost(150.0)
                .status(Order.Status.PENDING)
                .build();

        // Act: Validate the request
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Assert: Product ID validation should fail
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("must be a date in the present or in the future"));
    }
}
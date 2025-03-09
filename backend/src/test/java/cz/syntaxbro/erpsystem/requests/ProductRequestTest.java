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
 * Unit tests for the ProductRequest class.
 * Ensures that validation constraints are properly enforced.
 */
class ProductRequestTest {

    private static Validator validator;

    /**
     * Initializes the Validator instance before all tests.
     * The Validator is used to validate ProductRequest objects.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test case to validate a correctly formatted product request.
     * Expected: No validation errors.
     */
//    @Test
//    void shouldPassValidationWithValidProduct() {
//        // Arrange: Create a valid product request
//        ProductRequest request = new ProductRequest();
//        request.setName("Laptop");
//        request.setPrice(799.99);
//        request.setDescription("High-performance laptop");
//
//        // Act: Validate the request
//        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
//
//        // Assert: No validation errors should be present
//        assertThat(violations).isEmpty();
//    }

    /**
     * Test case to check validation failure when product name is blank.
     * Expected: Validation error for missing product name.
     */
//    @Test
//    void shouldFailValidationForBlankProductName() {
//        // Arrange: Create a product request with an empty name
//        ProductRequest request = new ProductRequest();
//        request.setName("");
//        request.setPrice(199.99);
//        request.setDescription("Gaming Mouse");
//
//        // Act: Validate the request
//        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
//
//        // Assert: Product name validation should fail
//        assertThat(violations).isNotEmpty();
//        assertThat(violations).anyMatch(v -> v.getMessage().contains("Product name is required"));
//    }

    /**
     * Test case to check validation failure when product price is null.
     * Expected: Validation error for missing product price.
     */
//    @Test
//    void shouldFailValidationForNullProductPrice() {
//        // Arrange: Create a product request with a null price
//        ProductRequest request = new ProductRequest();
//        request.setName("Smartphone");
//        request.setPrice(null); // This should trigger validation
//        request.setDescription("Latest model smartphone");
//
//        // Act: Validate the request
//        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
//
//        // 🔍 Debugging: Print validation messages to find out what’s happening
//        violations.forEach(v -> System.out.println("Validation message: " + v.getMessage()));
//
//        // Assert: Ensure validation error occurs for null price
//        assertThat(violations).isNotEmpty();
//        assertThat(violations)
//                .anyMatch(v -> v.getMessage().toLowerCase().contains("must not be null") // English message
//                        || v.getMessage().toLowerCase().contains("nesmí být null")); // Handles localization
//    }

    /**
     * Test case to check validation failure when product price is negative.
     * Expected: Validation error for negative product price.
     */
//    @Test
//    void shouldFailValidationForNegativeProductPrice() {
//        // Arrange: Create a product request with a negative price
//        ProductRequest request = new ProductRequest();
//        request.setName("Smartwatch");
//        request.setPrice(-50.0);
//        request.setDescription("Wearable smartwatch");
//
//        // Act: Validate the request
//        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
//
//        // Assert: Product price validation should fail
//        assertThat(violations).isNotEmpty();
//        assertThat(violations).anyMatch(v -> v.getMessage().contains("Product price must be greater than zero"));
//    }
}
package cz.syntaxbro.erpsystem.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for validating the Product entity.
 * This test ensures that the validation constraints on the Product model work correctly.
 */
@DataJpaTest
class ProductTest {

    private Validator validator;

    /**
     * Sets up the validator before each test execution.
     * The validator is used to check for constraint violations.
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test to verify that a valid product passes all validation constraints.
     */
    @Test
    void shouldCreateValidProduct() {
        // Arrange: Create a valid product instance
        Product product = Product.builder()
                .name("Test Product")
                .price(99.99)
                .description("A sample product")
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
    }

    /**
     * Test to verify that a product with a blank name fails validation.
     */
    @Test
    void shouldFailWhenNameIsBlank() {
        // Arrange: Create a product with a blank name
        Product product = Product.builder()
                .name("")  // Invalid: blank name
                .price(99.99)
                .description("A sample product")
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: The validation should fail due to missing name
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Product name is required");
    }

    /**
     * Test to verify that a product with a negative price fails validation.
     */
    @Test
    void shouldFailWhenPriceIsNegative() {
        // Arrange: Create a product with an invalid negative price
        Product product = Product.builder()
                .name("Valid Name")
                .price(-10.0)  // Invalid: negative price
                .description("A sample product")
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: The validation should fail due to a negative price
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Product price must be greater than zero");
    }

    /**
     * Test to verify that a product with a null description passes validation.
     */
    @Test
    void shouldAllowNullDescription() {
        // Arrange: Create a product with a null description
        Product product = Product.builder()
                .name("Valid Name")
                .price(50.0)
                .description(null)  // Allowed: description is nullable
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: No validation errors should be found since description is nullable
        assertThat(violations).isEmpty();
    }
}
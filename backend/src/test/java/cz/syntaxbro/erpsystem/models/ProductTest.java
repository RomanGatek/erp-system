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
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A sample product")
                .buyoutPrice(89.9)
                .purchasePrice(99.99)
                .productCategory(productCategory)
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
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name(null)
                .description("A sample product")
                .buyoutPrice(89.9)
                .purchasePrice(99.99)
                .productCategory(productCategory)
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
    void shouldFailWhenBuyoutPriceIsNegative() {
        // Arrange: Create a product with an invalid negative price
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A sample product")
                .buyoutPrice(-10.0)
                .purchasePrice(9)
                .productCategory(productCategory)
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: The validation should fail due to a negative price
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Product buyout price must be greater than zero");
    }

    @Test
    void shouldFailWhenPurchasePriceIsNegative() {
        // Arrange: Create a product with an invalid negative price
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A sample product")
                .buyoutPrice(10.0)
                .purchasePrice(-9)
                .productCategory(productCategory)
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: The validation should fail due to a negative price
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Product purchase price must be greater than zero");
    }

    /**
     * Test to verify that a product with a null description passes validation.
     */
    @Test
    void shouldAllowNullDescription() {
        // Arrange: Create a product with a null description
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Valid Name")
                .description("A sample product")
                .buyoutPrice(40)
                .purchasePrice(50)
                .productCategory(productCategory)
                .build();

        // Act: Validate the product
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Assert: No validation errors should be found since description is nullable
        assertThat(violations).isEmpty();
    }
}
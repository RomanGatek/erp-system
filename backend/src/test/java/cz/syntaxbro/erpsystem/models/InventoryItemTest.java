package cz.syntaxbro.erpsystem.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryItemTest {

    private Validator validator;

    private final Product product = Product.builder()
            .id(1L)
            .name("Test product")
            .purchasePrice(50.0)
            .buyoutPrice(30.0)
            .description("Sample product description")
            .build();


    @BeforeEach
    void setUp() {
        // Initializes the Jakarta Validator before each test
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test: Ensures validation fails when the item name is blank.
     * Expected Result:
     * - A constraint violation should be triggered for the 'name' field.
     */
    @Test
    void shouldFailValidation_WhenNameIsBlank() {
        // Arrange: Create an InventoryItem with an empty name
        InventoryItem item = InventoryItem.builder()
                .stockedAmount(10)
                .build();

        // Act: Validate the item
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);

        // Assert: Expect a validation error due to the empty name
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Product is required"));
    }

    /**
     * Test: Ensures validation fails when the quantity is negative.
     * Expected Result:
     * - A constraint violation should be triggered for the 'quantity' field.
     */
    @Test
    void shouldFailValidation_WhenQuantityIsNegative() {
        // Arrange: Create an InventoryItem with a negative quantity
        InventoryItem item = InventoryItem.builder()
                .product(product)
                .stockedAmount(-5)
                .build();

        // Act: Validate the item
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);

        // Assert: Expect a validation error due to negative quantity
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Item quantity must be zero or positive"));
    }

    /**
     * Test: Ensures validation passes when valid data is provided.
     * Expected Result:
     * - No constraint violations should occur.
     */
    @Test
    void shouldPassValidation_WhenValidData() {
        // Arrange: Create an InventoryItem with valid name and quantity
        InventoryItem item = InventoryItem.builder()
                .product(product)
                .stockedAmount(5)
                .build();

        // Act: Validate the item
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);

        // Assert: Expect no validation errors
        assertThat(violations).isEmpty();
    }
}

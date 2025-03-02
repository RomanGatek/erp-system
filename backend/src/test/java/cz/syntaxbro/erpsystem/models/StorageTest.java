package cz.syntaxbro.erpsystem.models;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the Storage entity.
 * Ensures that validation constraints work correctly and that Storage behaves as expected.
 */
class StorageTest {

    private Validator validator;

    /**
     * Initializes the validator before each test execution.
     * This validator will check if the constraints on Storage attributes are respected.
     */
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test to verify that a valid storage entity is created without validation errors.
     */
    @Test
    void shouldCreateValidStorage() {
        // Arrange: Create a sample product
        Product product = new Product(1L, "Test Product", 50.0, "Sample product description");

        // Create a valid Storage object
        Storage storage = new Storage(1L, product, 100);

        // Act: Validate the storage
        var violations = validator.validate(storage);

        // Assert: No validation errors should be found
        assertThat(violations).isEmpty();
        assertEquals(1L, storage.getId());
        assertEquals(product, storage.getProduct());
        assertEquals(100, storage.getQuantity());
    }

    /**
     * Test to verify that a storage entity with a null product passes validation.
     * This is because the product is not marked as @NotNull in the entity.
     */
    @Test
    void shouldAllowNullProduct() {
        // Arrange: Create a Storage object without a product
        Storage storage = new Storage(2L, null, 50);

        // Act: Validate the storage
        var violations = validator.validate(storage);

        // Assert: No validation errors should be found since product is not required
        assertThat(violations).isEmpty();
        assertEquals(2L, storage.getId());
        assertThat(storage.getProduct()).isNull();
        assertEquals(50, storage.getQuantity());
    }

    /**
     * Test to verify that a storage entity correctly stores a quantity value.
     */
    @Test
    void shouldStoreQuantityCorrectly() {
        // Arrange: Create a sample product
        Product product = new Product(3L, "Sample Product", 100.0, "Description");

        // Create a Storage object with a given quantity
        Storage storage = new Storage(3L, product, 250);

        // Act & Assert: Verify that the quantity is stored correctly
        assertThat(storage.getQuantity()).isEqualTo(250);
    }

    /**
     * Test to ensure that a storage entity can be initialized with zero quantity.
     */
    @Test
    void shouldAllowZeroQuantity() {
        // Arrange: Create a sample product
        Product product = new Product(4L, "Zero Stock Product", 30.0, "No stock yet");

        // Create a Storage object with zero quantity
        Storage storage = new Storage(4L, product, 0);

        // Act & Assert: Verify that zero quantity is allowed
        assertThat(storage.getQuantity()).isEqualTo(0);
    }
}
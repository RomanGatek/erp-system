package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for ProductRepository.
 * Ensures that database operations on the Product entity work as expected.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Sets up test data before each test.
     * A sample product is created and saved in the database.
     */
    @BeforeEach
    void setUp() {
        Product testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("A sample product for testing.")
                .buyoutPrice(89.9)
                .purchasePrice(99.99)
                .build();
        productRepository.save(testProduct);
    }

    /**
     * Test to ensure that a product can be retrieved by its name.
     */
    @Test
    void findByName_ShouldReturnProduct_WhenProductExists() {
        // Act: Retrieve product by name
        Optional<Product> foundProduct = productRepository.findByName("Test Product");

        // Assert: Ensure the product is found and matches expected values
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
        assertThat(foundProduct.get().getPurchasePrice()).isEqualTo(99.99);
        assertThat(foundProduct.get().getBuyoutPrice()).isEqualTo(89.99);
        assertThat(foundProduct.get().getDescription()).isEqualTo("A sample product for testing.");
    }

    /**
     * Test to verify that searching for a non-existent product returns an empty result.
     */
    @Test
    void findByName_ShouldReturnEmpty_WhenProductDoesNotExist() {
        // Act: Attempt to find a product that does not exist
        Optional<Product> foundProduct = productRepository.findByName("Non-Existent Product");

        // Assert: Ensure that the result is empty
        assertThat(foundProduct).isEmpty();
    }

    /**
     * Test to ensure that saving a product with a duplicate name violates database constraints.
     */
    @Test
    void save_ShouldThrowException_WhenProductNameIsNotUnique() {
        // Arrange: Create another product with the same name

        Product duplicateProduct = Product.builder()
                .name("Test Product")
                .description("Another product with the same name.")
                .buyoutPrice(40.00)
                .purchasePrice(50.00)
                .build();

        // Act & Assert: Expect a DataIntegrityViolationException due to unique constraint violation
        assertThrows(DataIntegrityViolationException.class, () -> productRepository.saveAndFlush(duplicateProduct));
    }
}
package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for ProductRepository.
 * Ensures that database operations on the Product entity work as expected.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private String uniqueName;

    /**
     * Sets up test data before each test.
     * A sample product is created and saved in the database.
     */
    @BeforeEach
    void setUp() {
        // Clean up previous test data
        productRepository.deleteAll();
        
        // Create unique name to prevent conflicts
        uniqueName = "Test Product " + UUID.randomUUID().toString().substring(0, 8);
        
        // Create product without specifying ID - let the database assign it
        this.testProduct = Product.builder()
                .name(uniqueName)
                .description("Test description")
                .buyoutPrice(10.0)
                .purchasePrice(20.0)
                .build();
                
        // Save the product
        this.testProduct = productRepository.save(testProduct);
    }

    /**
     * Test to ensure that a product can be retrieved by its name.
     */
    @Test
    void findByName_ShouldReturnProduct_WhenProductExists() {
        // Act: Retrieve product by name
        Optional<Product> foundProduct = productRepository.findByName(uniqueName);

        // Assert: Ensure the product is found and matches expected values
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo(uniqueName);
        assertThat(foundProduct.get().getPurchasePrice()).isEqualTo(20.0);
        assertThat(foundProduct.get().getBuyoutPrice()).isEqualTo(10.0);
        assertThat(foundProduct.get().getDescription()).isEqualTo("Test description");
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
                .name(uniqueName)
                .description("Another product with the same name.")
                .buyoutPrice(40.00)
                .purchasePrice(50.00)
                .build();

        // Act & Assert: Expect a DataIntegrityViolationException due to unique constraint violation
        assertThrows(DataIntegrityViolationException.class, () -> productRepository.saveAndFlush(duplicateProduct));
    }

    @Test
    void shouldFindProductById() {
        // When
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());
        
        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo(uniqueName);
    }
    
    @Test
    void shouldUpdateProduct() {
        // Given
        final String updatedDescription = "Updated description";
        testProduct.setDescription(updatedDescription);
        
        // When
        Product updatedProduct = productRepository.save(testProduct);
        
        // Then
        assertThat(updatedProduct.getDescription()).isEqualTo(updatedDescription);
        
        // Verify the update persisted
        Optional<Product> retrievedProduct = productRepository.findById(testProduct.getId());
        assertThat(retrievedProduct).isPresent();
        assertThat(retrievedProduct.get().getDescription()).isEqualTo(updatedDescription);
    }
    
    @Test
    void shouldDeleteProduct() {
        // When
        productRepository.delete(testProduct);
        
        // Then
        Optional<Product> deletedProduct = productRepository.findById(testProduct.getId());
        assertThat(deletedProduct).isEmpty();
    }
}
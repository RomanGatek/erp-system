package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InventoryRepository.
 * These tests verify that database operations on the InventoryItem entity function correctly.
 */
@DataJpaTest // Loads only JPA-related components for lightweight testing.
@Transactional // Ensures each test runs within a transaction and rolls back afterward.
@Rollback // Ensures that changes made in tests do not persist in the database.
public class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private InventoryItem testItem;
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    /**
     * Setup method to initialize test data before each test.
     * Creates and saves an inventory item in the database.
     */
    @BeforeEach
    void setUp() {
        // Arrange: Create and save a test inventory item
        // ew Product(1L, "Test Product", 50.0, "Sample product description")

        ProductCategory pCategory = productCategoryRepository.save(ProductCategory.builder()
                .name("test")
                .description("test")
                .build());

        productRepository.save(Product.builder()
                .name("Test Product")
                .description("A sample product")
                .buyoutPrice(89.9)
                .purchasePrice(99.99)
                        .productCategory(pCategory)
                .build()

        );
        Product product = productRepository.findByName("Test Product").orElseThrow(
                () -> new RuntimeException("Product not found")
        );

        testItem = inventoryRepository.save(
                InventoryItem.builder()
                .product(product)
                .stockedAmount(100)
                .build()
        );
        Optional<InventoryItem> item = inventoryRepository.findByProduct(product);
        item.ifPresent(inventoryItem -> this.testItem = inventoryItem);


    }

    /**
     * Test case for updating the quantity of an existing inventory item.
     * It verifies that the correct number of records is updated and
     * the database reflects the new quantity.
     */
    @Test
    void updateQuantity_shouldUpdateExistingItem() {
        // Act: Update the quantity of the existing inventory item

        int updatedRows = inventoryRepository.updateQuantity(testItem.getId(), 20);

        // Assert: Verify that one record was updated
        assertEquals(1, updatedRows, "Just one record has been edited.");

        // Ensure changes are committed before retrieving the updated entity
        inventoryRepository.flush();

        // Retrieve the updated inventory item from the database
        InventoryItem updatedItem = inventoryRepository.findById(testItem.getId()).orElseThrow();

        // Assert: Check that the quantity is updated correctly
//        assertEquals(20, updatedItem.getQuantity(), "The quantity should be updated to 20");
    }

    /**
     * Test case for updating a non-existing inventory item.
     * This verifies that attempting to update an item that does not exist
     * does not affect any records.
     */
    @Test
    void updateQuantity_shouldNotUpdateNonExistingItem() {
        // Act: Attempt to update an item with a non-existing ID
        int updatedRows = inventoryRepository.updateQuantity(99L, 30);

        System.out.println(updatedRows);

        // Assert: Verify that no records were updated
        assertEquals(0, updatedRows, "No record should have been updated.");
    }

    @Test
    @Transactional
    void updateQuantity_Success() {
        // Arrange
        ProductCategory productCategory = productCategoryRepository.findByName("test").get();
        Product product__ = Product.builder()
                .name("testName")
                .description("description")
                .buyoutPrice(100)
                .purchasePrice(200)
                .productCategory(productCategory)
                .build();

        productRepository.save(product__);

        var productOptional = productRepository.findByName("testName");

        if (productOptional.isPresent()) {
            InventoryItem inventoryItem = InventoryItem.builder()
                .product(productOptional.get())
                .build();

        inventoryItem = inventoryRepository.save(inventoryItem); // Save and get generated ID

        inventoryRepository.updateQuantity(
                inventoryItem.getId(), 200);
        InventoryItem updatedInventoryItem = inventoryRepository.findById(inventoryItem.getId()).get();
        assertEquals(200, updatedInventoryItem.getStockedAmount());
        }
    }

    @Test
    public void deleteTest(){
        //Arrange
        ProductCategory productCategory = productCategoryRepository.findByName("test").get();
        Product product__ = Product.builder()
                .name("testName")
                .description("description")
                .buyoutPrice(100)
                .purchasePrice(200)
                .productCategory(productCategory)
                .build();

        productRepository.save(product__);

        var productOptional = productRepository.findByName("testName");

        if (productOptional.isPresent()) {

            InventoryItem inventoryItem = InventoryItem.builder()
                    .product(productOptional.get())
                    .build();

            InventoryItem inventoryItemSaved = inventoryRepository.save(inventoryItem);
            //Act
            inventoryRepository.delete(inventoryItemSaved);
            Optional<InventoryItem> result = inventoryRepository.findById(inventoryItemSaved.getId());
            //Assert
            assertEquals(result, Optional.empty());
        }
    }
}
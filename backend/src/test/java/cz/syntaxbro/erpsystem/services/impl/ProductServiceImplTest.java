package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.repositories.ProductCategoryRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// Configure MockitoExtension to use LENIENT strictness to avoid stubbing argument mismatch issues
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    private Product testProduct;
    private ProductCategory category;

    @BeforeEach
    void setUp() {
        // Configure Mockito to use LENIENT strictness for this test class
        lenient().when(productCategoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        
        this.category = ProductCategory
                .builder()
                .name("default")
                .description("Sample product description")
                .build();

        this.testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Sample product description")
                .buyoutPrice(10)
                .purchasePrice(20)
                .build();
    }

    /**
     * Test: Create a product
     * Expected result: The saved product is returned
     */
    @Test
    void createProduct_shouldReturnSavedProduct() {
        doReturn(testProduct).when(productRepository).save(any(Product.class));
        doReturn(Optional.of(category)).when(productCategoryRepository).findByName("default");

        var request = ProductRequest.builder()
            .name(testProduct.getName())
            .description(testProduct.getDescription())
            .purchasePrice(testProduct.getPurchasePrice())
            .buyoutPrice(testProduct.getBuyoutPrice())
            .productCategory("default")
            .build();

        Product savedProduct = productService.createProduct(request);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test: Get product by ID
     * Expected result: Returns correct product
     */
    @Test
    void getProductById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductById(1L);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * Test: Attempt to get non-existent product by ID
     * Expected result: Throw EntityNotFoundException
     */
    @Test
    void getProductById_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found with id: 2");

        verify(productRepository, times(1)).findById(2L);
    }

    /**
     * Test: Product update
     * Expected result: Returns updated product
     */
    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPurchasePrice(129.99);
        updatedProduct.setDescription("Updated Description");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(1L, updatedProduct);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPurchasePrice()).isEqualTo(129.99);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test: Deleting an existing product by ID
     * Expected result: Product deleted successfully
     */
    @Test
    void deleteProductById_shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProductById(1L);

        verify(productRepository, times(1)).delete(testProduct);
    }

    /**
     * Test: Attempt to delete a non-existent product
     * Expected result: Throw EntityNotFoundException
     */
    @Test
    void deleteProductById_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProductById(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found with id: 2");

        verify(productRepository, times(1)).findById(2L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    /**
     * Test: Checks if a product exists by ID
     * Expected result: Returns true if it exists, false if it doesn't
     */
    @Test
    void isExistById_shouldReturnTrue_whenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        boolean exists = productService.isExistById(1L);

        assertThat(exists).isTrue();
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * Test: Check for product existence by ID when product does not exist
     * Expected result: Returns false
     */
    @Test
    void isExistById_shouldReturnFalse_whenProductDoesNotExist() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        boolean exists = productService.isExistById(2L);

        assertThat(exists).isFalse();
        verify(productRepository, times(1)).findById(2L);
    }
}

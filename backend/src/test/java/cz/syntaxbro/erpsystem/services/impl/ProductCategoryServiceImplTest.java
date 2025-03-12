package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.repositories.ProductCategoryRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.ProductCategoryRequest;
import cz.syntaxbro.erpsystem.services.ProductCategoryService;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    @InjectMocks
    ProductCategoryServiceImpl productCategoryService;

    private ProductCategoryRequest productCategoryRequest;
    private ProductCategory productCategory;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample request
        productCategoryRequest = ProductCategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices and gadgets")
                .color("Blue")
                .build();

        // Expected saved entity
        productCategory = ProductCategory.builder()
                .name("Electronics")
                .description("Electronic devices and gadgets")
                .color("Blue")
                .build();
    }

    @Test
    void createProductCategory_Success() {
        // Arrange
        when(productCategoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

        // Act
        ProductCategory result = productCategoryService.createProductCategory(productCategoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals("Electronic devices and gadgets", result.getDescription());
        assertEquals("Blue", result.getColor());

        verify(productCategoryRepository, times(1)).findByName("Electronics");
        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }


    @Test
    void createProductCategory_ExistingCategory() {
        //Arrest
        when(productCategoryRepository.findByName("Electronics")).thenReturn(Optional.of(productCategory));
        //Act
        EntityExistsException exceptionResult = assertThrows(EntityExistsException.class, () ->
                productCategoryService.createProductCategory(productCategoryRequest));
        //Arrest
        assertEquals(String.format("Category with name %s exist", productCategory.getName()), exceptionResult.getMessage());
    }

    @Test
    void updateProductCategory() {
        //Arrest
        this.productCategoryRequest.setName("Computers");
        long id = 1L;
        when(productCategoryRepository.findById(id)).thenReturn(Optional.of(productCategory));
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);
        //Act
        ProductCategory result = productCategoryService.updateProductCategory(id, productCategoryRequest);
        assertEquals("Computers", result.getName());
        verify(productCategoryService, times(1)).updateProductCategory(id, productCategoryRequest);
    }

    @Test
    void deleteProductCategory_Successfully() {
        //Arrest
        long id = 1L;
        when(productCategoryRepository.findById(id)).thenReturn(Optional.of(productCategory));
        //Act
        productCategoryService.deleteProductCategory(id);
        //Assert
        assertEquals(0, productCategoryRepository.findAll().size());
        verify(productCategoryService, times(1)).deleteProductCategory(id);
    }

    @Test
    void getProductCategory_successfully() {
        //Arrest
        long id = 1L;
        when(productCategoryRepository.findById(id)).thenReturn(Optional.of(productCategory));
        //Act
        ProductCategory result = productCategoryService.getProductCategory(id);
        //Assert
        assertNotNull(result);
        assertEquals(this.productCategory, result);
        verify(productCategoryRepository, times(1)).findById(id);
    }

    @Test
    void getProductCategory_NotFound() {
        //Arrest
        long id = 1L;
        when(productCategoryRepository.findById(id)).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                productCategoryService.getProductCategory(id));
        //Assert
        assertEquals("Category not found with id: " + id, exceptionResult.getMessage());
        verify(productCategoryRepository, times(1)).findById(id);
    }

    @Test
    void getProductCategories() {
        //Arrest
        when(productCategoryRepository.findAll()).thenReturn(List.of(this.productCategory));
        //Act
        List<ProductCategory> result = productCategoryService.getProductCategories();
        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productCategoryRepository, times(1)).findAll();
        verify(productCategoryService, times(1)).getProductCategories();
    }
}
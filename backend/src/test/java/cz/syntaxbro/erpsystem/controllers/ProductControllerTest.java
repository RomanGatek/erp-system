package cz.syntaxbro.erpsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product goodProduct;
    private Product badProduct;
    private Product updatedProduct;

    @BeforeEach
    void setUp() {
        goodProduct = new Product(1L, "Product Name", 12.12, "Good Product");
        badProduct = new Product(1L, "", 12.12, "Bad Product");
        updatedProduct = new Product(1L, "New Product Name", 12.13, "Updated Product");
    }

    /**
     * Test case: Attempting to create a new product.
     * Expected result: HTTP 201.
     */
    @Test
    @WithMockUser(username = "admin")
    void createProductOkTest() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenReturn(goodProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodProduct)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    /**
     * Test case: Attempting to create a new product with bad product name.
     * Expected result: HTTP 400.
     */
    @Test
    @WithMockUser(username = "admin")
    void createProductWrongDataTest() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenReturn(badProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).createProduct(any(Product.class));
    }

    /**
     * Test case: Attempting to get a product by id 1.
     * Expected result: HTTP 200.
     */
    @Test
    @WithMockUser(username = "admin")
    void getProductByIdOkTest() throws Exception {
        when(productService.getProductById(goodProduct.getId()))
                .thenReturn(goodProduct);

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goodProduct.getId()))
                .andExpect(jsonPath("$.name").value(goodProduct.getName()))
                .andExpect(jsonPath("$.price").value(goodProduct.getPrice()))
                .andExpect(jsonPath("$.description").value(goodProduct.getDescription()));

        verify(productService, times(1))
                .getProductById(goodProduct.getId());
    }

    /**
     * Test case: Attempting to get a product by name.
     * Expected result: HTTP 200.
     */
    @Test
    @WithMockUser(username = "admin")
    void getProductByNameOkTest() throws Exception {
        when(productService.getProductByName(goodProduct.getName()))
                .thenReturn(goodProduct);

        mockMvc.perform(get("/api/products/name/" + goodProduct.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goodProduct.getId()))
                .andExpect(jsonPath("$.name").value(goodProduct.getName()))
                .andExpect(jsonPath("$.price").value(goodProduct.getPrice()))
                .andExpect(jsonPath("$.description").value(goodProduct.getDescription()));

        verify(productService, times(1))
                .getProductByName(goodProduct.getName());
    }

    /**
     * Test case: Attempting to update a product.
     * Expected result: HTTP 200.
     */
    @Test
    @WithMockUser(username = "admin")
    void updateProductOkTest() throws Exception {
        when(productService.updateProduct(updatedProduct.getId(), updatedProduct))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/" + goodProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedProduct.getId()))
                .andExpect(jsonPath("$.name").value(updatedProduct.getName()))
                .andExpect(jsonPath("$.price").value(updatedProduct.getPrice()))
                .andExpect(jsonPath("$.description").value(updatedProduct.getDescription()));

        verify(productService, times(1))
                .updateProduct(updatedProduct.getId(), updatedProduct);
    }

    /**
     * Test case: Attempting to delete a product by id.
     * Expected result: HTTP 204.
     */
    @Test
    @WithMockUser(username = "admin")
    void deleteProductByIdOkTest() throws Exception {
        doNothing().when(productService).deleteProductById(goodProduct.getId());

        mockMvc.perform(delete("/api/products/" + goodProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1))
                .deleteProductById(goodProduct.getId());
    }

    /**
     * Test case: Attempting to delete a product by name.
     * Expected result: HTTP 204.
     */
    @Test
    @WithMockUser(username = "admin")
    void deleteProductByNameOkTest() throws Exception {
        doNothing().when(productService).deleteProductByName(goodProduct.getName());

        mockMvc.perform(delete("/api/products/name/" + goodProduct.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1))
                .deleteProductByName(goodProduct.getName());
    }
}
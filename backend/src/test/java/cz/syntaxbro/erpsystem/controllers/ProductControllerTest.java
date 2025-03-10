package cz.syntaxbro.erpsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.requests.ProductRequest;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
       // Create the product request for tests
       this.testProductRequest = ProductRequest.builder()
               .description("sort description")
               .name("testName")
               .purchasePrice(20.0)
               .buyoutPrice(10.0)
               .build();
               
       // Create and save a test product to the database
       Product product = Product.builder()
               .description("sort description")
               .name("testProduct-" + System.currentTimeMillis()) // Ensure unique name
               .purchasePrice(20.0)
               .buyoutPrice(10.0)
               .build();
               
       this.testProduct = productService.createProduct(product);
    }

    /**
     * Test: Create a new product
     * Expected result: HTTP 201 (CREATED)
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProductOkTest() throws Exception {
        ProductRequest productRequest = this.testProductRequest;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.purchasePrice").value(20.0));
    }

    /**
     * Test: Get product by ID
     * Expected result: HTTP 200 (OK)
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductById_shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    /**
     * Test: Deleting a product
     * Expected result: HTTP 204 (NO CONTENT)
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProductByIdOkTest() throws Exception {
        mockMvc.perform(delete("/api/products/" + testProduct.getId()))
                .andExpect(status().isNoContent());
    }
}
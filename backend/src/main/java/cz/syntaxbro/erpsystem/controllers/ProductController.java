package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Object requestBody) {

            // Validácia dátových typov
            if (!(requestBody instanceof Map)) {
                throw new IllegalArgumentException("Invalid request format");
            }

            Map<String, Object> productData = (Map<String, Object>) requestBody;

            // Validácia name
            if (!productData.containsKey("name")) {
                throw new IllegalArgumentException("Name is required");
            }
            if (!(productData.get("name") instanceof String)) {
                throw new IllegalArgumentException("Name must be a string");
            }

            // Validácia cost
            if (!productData.containsKey("cost")) {
                throw new IllegalArgumentException("Cost is required");
            }
            if (!(productData.get("cost") instanceof Double)) {
                throw new IllegalArgumentException("Cost must be a double");
            }

            // Validácia quantity
            if (!productData.containsKey("quantity")) {
                throw new IllegalArgumentException("Quantity is required");
            }
            if (!(productData.get("quantity") instanceof Integer)) {
                throw new IllegalArgumentException("Quantity must be an integer");
            }

            // Vytvorenie produktu po úspešnej validácii
            Product product = new Product();
            product.setName((String) productData.get("name"));
            product.setCost(((Number) productData.get("cost")).doubleValue());
            product.setQuantity(((Number) productData.get("quantity")).intValue());

            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable(name = "name") String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "id") Long id,@Valid @RequestBody Object requestBody) {

            // Validácia dátových typov
            if (!(requestBody instanceof Map)) {
                throw new IllegalArgumentException("Invalid request format");
            }

            Map<String, Object> productData = (Map<String, Object>) requestBody;

            // Validácia name
            if (!productData.containsKey("name")) {
                throw new IllegalArgumentException("Name is required");
            }
            if (!(productData.get("name") instanceof String)) {
                throw new IllegalArgumentException("Name must be a string");
            }

            // Validácia cost
            if (!productData.containsKey("cost")) {
                throw new IllegalArgumentException("Cost is required");
            }
            if (!(productData.get("cost") instanceof Double)) {
                throw new IllegalArgumentException("Cost must be a number");
            }

            // Validácia quantity
            if (!productData.containsKey("quantity")) {
                throw new IllegalArgumentException("Quantity is required");
            }
            if (!(productData.get("quantity") instanceof Integer)) {
                throw new IllegalArgumentException("Quantity must be an integer");
            }

            // Získanie existujúceho produktu
            Product existingProduct = productService.getProductById(id);

            // Aktualizácia len poskytnutých polí
            if (productData.containsKey("name")) {
                existingProduct.setName((String) productData.get("name"));
            }
            if (productData.containsKey("cost")) {
                existingProduct.setCost(((Number) productData.get("cost")).doubleValue());
            }
            if (productData.containsKey("quantity")) {
                existingProduct.setQuantity(((Number) productData.get("quantity")).intValue());
            }

            Product updatedProduct = productService.updateProduct(id, existingProduct);
            return ResponseEntity.ok(updatedProduct);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable(name = "id") Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteProductByName(@PathVariable(name = "name") String name) {
        productService.deleteProductByName(name);
        return ResponseEntity.noContent().build();
    }
}
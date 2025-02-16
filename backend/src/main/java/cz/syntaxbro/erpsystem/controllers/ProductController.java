package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.services.ProductService;
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
    public ResponseEntity<?> createProduct(@RequestBody Object requestBody) {
        try {
            // Validácia dátových typov
            if (!(requestBody instanceof Map)) {
                return ResponseEntity.badRequest().body("Invalid request format");
            }

            Map<String, Object> productData = (Map<String, Object>) requestBody;

            // Validácia name
            if (!productData.containsKey("name") || !(productData.get("name") instanceof String)) {
                return ResponseEntity.badRequest().body("Name must be a string");
            }

            // Validácia cost
            if (!productData.containsKey("cost") ||
                    !(productData.get("cost") instanceof Number) ||
                    ((Number) productData.get("cost")).doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Cost must be a positive number of type double");
            }

            // Validácia quantity
            if (!productData.containsKey("quantity") ||
                    !(productData.get("quantity") instanceof Number) ||
                    ((Number) productData.get("quantity")).intValue() < 0) {
                return ResponseEntity.badRequest().body("Quantity must be a zero or positive integer");
            }

            // Vytvorenie produktu po úspešnej validácii
            Product product = new Product();
            product.setName((String) productData.get("name"));
            product.setCost(((Number) productData.get("cost")).doubleValue());
            product.setQuantity(((Number) productData.get("quantity")).intValue());

            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Object requestBody) {
        try {
            // Validácia dátových typov
            if (!(requestBody instanceof Map)) {
                return ResponseEntity.badRequest().body("Invalid request format");
            }

            Map<String, Object> productData = (Map<String, Object>) requestBody;

            // Validácia name (ak je poskytnutý)
            if (productData.containsKey("name") && !(productData.get("name") instanceof String)) {
                return ResponseEntity.badRequest().body("Name must be a string");
            }

            // Validácia cost (ak je poskytnutý)
            if (productData.containsKey("cost") &&
                    (!(productData.get("cost") instanceof Number) ||
                            ((Number) productData.get("cost")).doubleValue() <= 0)) {
                return ResponseEntity.badRequest().body("Cost must be a positive number");
            }

            // Validácia quantity (ak je poskytnutý)
            if (productData.containsKey("quantity") &&
                    (!(productData.get("quantity") instanceof Number) ||
                            ((Number) productData.get("quantity")).intValue() < 0)) {
                return ResponseEntity.badRequest().body("Quantity must be a zero or positive integer");
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

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteProductByName(@PathVariable String name) {
        productService.deleteProductByName(name);
        return ResponseEntity.noContent().build();
    }
}
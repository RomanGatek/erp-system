package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.ProductRequest;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest productRequest) {

        ErpSystemApplication.getLogger().info("\u001B[32mCreating product: {}\u001B[0m", productRequest);

        Product createdProduct = productService.createProduct(
                Product.builder()
                        .name(productRequest.getName())
                        .buyoutPrice(productRequest.getBuyoutPrice())
                        .purchasePrice(productRequest.getPurchasePrice())
                        .description(productRequest.getDescription())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
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
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deleteProductByName(@PathVariable String name) {
        productService.deleteProductByName(name);
        return ResponseEntity.noContent().build();
    }
}
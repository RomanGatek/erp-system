package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.services.ProductCategoryService;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@EnableMethodSecurity()
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getProductCategoryById(@PathVariable long id) {
        return ResponseEntity.ok(
                productCategoryService.getProductCategory(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> findAll() {
        return ResponseEntity.ok(
                productCategoryService.getProductCategories()
        );
    }

    @PostMapping()
    public ResponseEntity<ProductCategory> create(@RequestBody ProductCategory productCategory) {
        ProductCategory savedProductCategory =  productCategoryService.createProductCategory(productCategory);
        return ResponseEntity.ok(savedProductCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(
            @PathVariable long id,
            @RequestBody ProductCategory productCategory) {

        return ResponseEntity.ok(
                productCategoryService.updateProductCategory(id, productCategory)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        productCategoryService.deleteProductCategory(id);
        return ResponseEntity.noContent().build();
    }
}

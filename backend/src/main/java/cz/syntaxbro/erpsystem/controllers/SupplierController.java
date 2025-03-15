package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Supplier;
import cz.syntaxbro.erpsystem.services.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@EnableMethodSecurity()
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getProductCategoryById(@PathVariable long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getSuppliers());
    }

    @PostMapping()
    public ResponseEntity<Supplier> create(@RequestBody Supplier supplier) {
        Supplier savedSupplier = supplierService.createSupplier(supplier);
        return ResponseEntity.ok(savedSupplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable long id,
                                                   @RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}

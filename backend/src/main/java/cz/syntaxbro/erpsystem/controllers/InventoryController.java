package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Validated
@EnableMethodSecurity()
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryItem> getItem(@PathVariable @Min(1) Long itemId) {
        return ResponseEntity.ok(inventoryService.getItem(itemId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryItem> addItem(@Valid @RequestBody InventoryItem item) {
        InventoryItem savedItem = inventoryService.addItem(item);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryItem> updateItem(
            @PathVariable @Min(value = 1, message = "Must be positive number") Long itemId,
            @Valid @RequestBody InventoryItem item
    ) {
        var savedItem = inventoryRepository.findById(itemId);
        if (savedItem.isPresent()) {
            var item_ = savedItem.get();
            if (item.getProduct() != null) item_.setProduct(item.getProduct());
            item_.setQuantity(item.getQuantity());
            item_ = inventoryRepository.save(item_);
            return ResponseEntity.status(HttpStatus.OK).body(item_);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        ErpSystemApplication.getLogger().info("Trying to get all items");
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> removeItem(
            @PathVariable @Min(value = 1, message = "Must be positive number") Long itemId
    ) {
        var savedItem = inventoryRepository.findById(itemId);
        if (savedItem.isPresent()) {
            inventoryRepository.delete(savedItem.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
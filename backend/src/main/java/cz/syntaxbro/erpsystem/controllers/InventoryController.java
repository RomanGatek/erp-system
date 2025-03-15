package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.requests.InventoryItemRequest;
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
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
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
    public ResponseEntity<InventoryItem> getItem(@PathVariable @Min(1) Long itemId) {
        return ResponseEntity.ok(inventoryService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<InventoryItem> addItem(@Valid @RequestBody InventoryItemRequest item) {
        InventoryItem savedItem = inventoryService.addItem(item);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<InventoryItem> updateItem(
            @PathVariable @Min(value = 1, message = "Must be positive number") Long itemId,
            @Valid @RequestBody InventoryItemRequest item
    ) {
        var savedItem = inventoryService.updateItem(itemId, item);
        return ResponseEntity.ok(savedItem);
    }


    @DeleteMapping("/{itemId}")
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
package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.services.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<InventoryItem> getItem(@PathVariable @Min(1) Long itemId) {
        return ResponseEntity.ok(inventoryService.getItem(itemId));
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryItem> addItem(@Valid @RequestBody InventoryItem item) {
        System.out.println(item);
        InventoryItem savedItem = inventoryService.addItem(item);

        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{itemId}/update")
    public ResponseEntity<String> updateQuantity(
            @PathVariable @Min(value = 1, message = "Must be positive number") Long itemId,
            @RequestParam @Min(value = 0, message = "Must be positive number") int quantity) {
        try {
            inventoryService.updateQuantity(itemId, quantity);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Cannot update quantity for id %d.", itemId));
        }

        return ResponseEntity.ok("Quantity updated successfully.");
    }
}
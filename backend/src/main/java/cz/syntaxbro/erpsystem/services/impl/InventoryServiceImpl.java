package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryItem getItem(Long itemId) {
        Optional<InventoryItem> item = inventoryRepository.findById(itemId);

        return item.orElseThrow(() -> new EntityNotFoundException(String.format("Item with id %d not found", itemId)));
    }

    public InventoryItem addItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Long itemId, int quantity) {
        int rows = inventoryRepository.updateQuantity(itemId, quantity);

        if (rows == 0) {
            throw new EntityNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }
}
package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryItem addItem(InventoryItem item) {
        var dbItem = inventoryRepository.findByProduct(item.getProduct());
        if (dbItem.isPresent()) {
            throw new DataIntegrityViolationException(String.format("Duplicate entry '%s' for key 'xxxx.UKo61fmio5yukmmiqgnxf8pnavn'", item.getProduct().getName()));
        } else {
            return inventoryRepository.save(item);
        }
    }

    @Transactional
    @Override
    public void reserveStock(Long itemId, int quantity) {
        InventoryItem item = this.getItem(itemId);
        int newQuantity = item.getStockedAmount() - quantity;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("We do not have enough quantity of this item.");
        }

        int rows = inventoryRepository.updateQuantity(itemId, newQuantity);

        if (rows == 0) {
            throw new EntityNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    @Transactional
    @Override
    public void updateQuantity(Long itemId, int quantity) {
        int rows = inventoryRepository.updateQuantity(itemId, quantity);

        if (rows == 0) {
            throw new EntityNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    @Override
    public List<InventoryItem> getAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public void receiveStock(Long itemId, int quantity) {
        InventoryItem inventoryItem = getItem(itemId);
        if (inventoryItem != null) {
            inventoryItem.setStockedAmount(inventoryItem.getStockedAmount() + quantity);
            inventoryRepository.save(inventoryItem);
        }
    }

    @Override
    public void releaseStock(Long itemId, int quantity) {
        InventoryItem inventoryItem = getItem(itemId);
        if (inventoryItem != null) {
            if (inventoryItem.getStockedAmount() < quantity) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "not enough quantity of product");
            }
            inventoryItem.setStockedAmount(inventoryItem.getStockedAmount() - quantity);
            inventoryRepository.save(inventoryItem);
        }
    }

    @Override
    public boolean isStockAvailable(Long itemId, int quantity) {
        InventoryItem item = this.getItem(itemId);

        return item.getStockedAmount() >= quantity;
    }

    @Override
    public InventoryItem findItemByProduct(Product product) {
        return inventoryRepository.findByProduct(product).orElseThrow(
                () -> new EntityNotFoundException(String.format("Inventory item for product '%s' not found", product.getName()))
        );
    }


    public InventoryItem getItem(Long itemId) {
        return inventoryRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item with id %d not found", itemId)));
    }
}
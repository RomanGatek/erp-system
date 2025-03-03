package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.InventoryItem;
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
        var dbItem = inventoryRepository.findByProduct(item.getProduct());
        if (dbItem.isPresent()){
            throw new DataIntegrityViolationException(String.format("Duplicate entry '%s' for key 'xxxx.UKo61fmio5yukmmiqgnxf8pnavn'", item.getProduct().getName()));
        } else {
            return inventoryRepository.save(item);
        }
    }

    @Transactional
    public void updateQuantity(Long itemId, int quantity) {
        int rows = inventoryRepository.updateQuantity(itemId, quantity);

        if (rows == 0) {
            throw new EntityNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    @Override
    public List<InventoryItem> getAll() {
        return List.of();
    }

    @Override
    public void receiveStock(Long itemId, int quantity) {
        InventoryItem inventoryItem = getItem(itemId);
        if (inventoryItem != null) {
            inventoryItem.setQuantity(inventoryItem.getQuantity() + quantity);
            inventoryRepository.save(inventoryItem);
        }
    }

    @Override
    public void releaseStock(Long itemId, int quantity) {
        InventoryItem inventoryItem = getItem(itemId);
        if (inventoryItem != null) {
            if(inventoryItem.getQuantity() < quantity) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "not enough quantity of product");
            }
            inventoryItem.setQuantity(inventoryItem.getQuantity() - quantity);

            System.out.println(quantityWarning());

            if(inventoryItem.getQuantity() < quantityWarning()) {
                inventoryRepository.save(inventoryItem);
                System.out.println(inventoryItem);
                throw new ResponseStatusException(HttpStatus.OK, "last pieces in stock");
            }
            inventoryRepository.save(inventoryItem);
        }
    }

    private int supplierDeliveryDelay() {
        return 7;
    }

    private int averageDailySail() {
        return 50;
    }

    private int quantityWarning(){
        return averageDailySail() * supplierDeliveryDelay();
    }
}
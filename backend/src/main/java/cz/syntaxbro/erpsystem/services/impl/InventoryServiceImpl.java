package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.StockOrder;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.repositories.StockOrderRepository;
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
    private final StockOrderRepository stockOrderRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, StockOrderRepository stockOrderRepository) {
        this.inventoryRepository = inventoryRepository;
        this.stockOrderRepository = stockOrderRepository;
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
        int newQuantity = item.getQuantity() - quantity;

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
            if (inventoryItem.getQuantity() < quantity) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "not enough quantity of product");
            }
            inventoryItem.setQuantity(inventoryItem.getQuantity() - quantity);

            if (inventoryItem.getQuantity() < quantityWarning()) {
                inventoryRepository.save(inventoryItem);
                System.out.println(inventoryItem);
                throw new ResponseStatusException(HttpStatus.OK, "last pieces in stock");
            }
            inventoryRepository.save(inventoryItem);
        }
    }

    @Override
    public boolean isStockAvailable(Long itemId, int quantity) {
        InventoryItem item = this.getItem(itemId);

        return item.getQuantity() >= quantity;
    }

    @Override
    public InventoryItem findItemByProduct(Product product) {
        return inventoryRepository.findByProduct(product).orElseThrow(
                () -> new EntityNotFoundException(String.format("Inventory item for product '%s' not found", product.getName()))
        );
    }

    @Override
    public void createOrderToStock(Long itemId, StockOrder stockOrderDto) {
        InventoryItem item = this.getItem(itemId);
        stockOrderDto.setProduct(item.getProduct());
        stockOrderDto.setStatus(Order.Status.PENDING);
        stockOrderRepository.save(stockOrderDto);
    }

    public StockOrder getStockOrderById(Long id) {
        return stockOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order with id %s not found", id)));
    }

    private int supplierDeliveryDelay() {
        return 7;
    }

    private int averageDailySail() {
        return 50;
    }

    private int quantityWarning() {
        return averageDailySail() * supplierDeliveryDelay();
    }


    public InventoryItem getItem(Long itemId) {
        return inventoryRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item with id %d not found", itemId)));
    }
}
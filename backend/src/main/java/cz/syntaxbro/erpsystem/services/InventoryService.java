package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;

import java.util.List;
import java.util.Optional;

public interface InventoryService {
    InventoryItem getItem(Long itemId);
    InventoryItem addItem(InventoryItem item);
    List<InventoryItem> getAll();
    InventoryItem findItemByProduct(Product product);
    Optional<InventoryItem> findItemByProductForOrder(Product product);

    void updateQuantity(Long itemId, int quantity);

    InventoryItem updateItem(Long id, InventoryItem item);

    void reserveStock(Long itemId, int quantity);
    void receiveStock(Long itemId, int quantity);
    void releaseStock(Long itemId, int quantity);
    boolean isStockAvailable(Long itemId, int quantity);
}
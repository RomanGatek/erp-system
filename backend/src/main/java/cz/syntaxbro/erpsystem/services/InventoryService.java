package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;

import java.util.List;

public interface InventoryService {
    InventoryItem getItem(Long itemId);
    InventoryItem addItem(InventoryItem item);
    List<InventoryItem> getAll();
    InventoryItem findItemByProduct(Product product);

    void updateQuantity(Long itemId, int quantity);

    void reserveStock(Long itemId, int quantity);
    void receiveStock(Long itemId, int quantity);
    void releaseStock(Long itemId, int quantity);
    boolean isStockAvailable(Long itemId, int quantity);
}
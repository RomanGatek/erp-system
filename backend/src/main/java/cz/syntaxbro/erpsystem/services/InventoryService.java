package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.StockOrder;

import java.util.List;

public interface InventoryService {
    InventoryItem getItem(Long itemId);
    InventoryItem addItem(InventoryItem item);
    void updateQuantity(Long itemId, int quantity);
    void reserveStock(Long itemId, int quantity);
    List<InventoryItem> getAll();
    void receiveStock(Long itemId, int quantity);
    void releaseStock(Long itemId, int quantity);
    boolean isStockAvailable(Long itemId, int quantity);
    InventoryItem findItemByProduct(Product product);
    void createOrderToStock(Long itemId, StockOrder stockOrderDto);
    StockOrder getStockOrderById(Long id);
}
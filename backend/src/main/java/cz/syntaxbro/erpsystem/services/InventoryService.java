package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;

import java.util.List;

public interface InventoryService {
    InventoryItem getItem(Long itemId);
    InventoryItem addItem(InventoryItem item);
    void updateQuantity(Long itemId, int quantity);
    List<InventoryItem> getAll();
    void receiveStock(Long itemId, int quantity);
    void releaseStock(Long itemId, int quantity);
}
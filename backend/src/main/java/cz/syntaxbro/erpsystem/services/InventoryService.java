package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;

public interface InventoryService {
    InventoryItem getItem(Long itemId);
    InventoryItem addItem(InventoryItem item);
    void updateQuantity(Long itemId, int quantity);
}
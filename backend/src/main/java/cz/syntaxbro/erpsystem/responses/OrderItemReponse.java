package cz.syntaxbro.erpsystem.responses;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.OrderItem;
import lombok.Data;

@Data
public class OrderItemReponse {
    private Long id;
    private Long inventoryItemId;
    private Long productId;
    private double stockedQuantity;
    private int needQuantity;
    private String name;
    private double buyoutPrice;
    private double purchasePrice;
    private String description;

    public OrderItemReponse(OrderItem item) {
        InventoryItem inventoryItem = item.getInventoryItem();

        this.setId(item.getId());
        this.setInventoryItemId(inventoryItem.getId());
        this.setProductId(inventoryItem.getProduct().getId());
        this.setStockedQuantity(inventoryItem.getStockedAmount());
        this.setNeedQuantity(item.getQuantity());
        this.setName(inventoryItem.getProduct().getName());
        this.setBuyoutPrice(inventoryItem.getProduct().getBuyoutPrice());
        this.setPurchasePrice(inventoryItem.getProduct().getPurchasePrice());
        this.setDescription(inventoryItem.getProduct().getDescription());
    }
}

package cz.syntaxbro.erpsystem.services;


import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;

import java.util.List;

public interface OrderItemService {
    OrderItem getOrderItemById(Long id);
    List<OrderItemReponse> getOrderItemsByOrderId(Long orderId);
    void deleteOrderItemById(Long id);
    void createOrderItem(OrderItem orderItem);
}

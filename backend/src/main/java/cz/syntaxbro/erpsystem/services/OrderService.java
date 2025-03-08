package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order getOrderById(Long id);

    List<OrderResponse> getOrders();

    List<Order> getOrdersByProduct(Long productId);

    Order createdOrder(Order order);

    void updateOrder(Long orderId, OrderRequest orderDto);

    void deleteOrder(Long id);

    // New workflow-related methods
    Order confirmOrder(Long id, String comment);
    Order cancelOrder(Long id, String comment);
    Order addWorkflowComment(Long id, String comment);
    List<Order> getOrdersByStatus(Order.Status status);
    ResponseEntity<?> updateOrderStatus(Long orderId, Order.Status status);
}

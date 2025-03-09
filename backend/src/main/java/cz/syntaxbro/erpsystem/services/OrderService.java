package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.requests.OrderUpdateRequest;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    Order getOrderById(Long id);

    List<OrderResponse> getOrders();

    List<Order> getOrdersByProduct(Long productId);

    Order createdOrder(OrderCreateRequest orderRequest);

    Order updateOrder(Long orderId, OrderUpdateRequest orderRequest);

    void deleteOrder(Long id);

    // New workflow-related methods
    Order confirmOrder(Long id, String comment);
    Order cancelOrder(Long id, String comment);
    Order addWorkflowComment(Long id, String comment);
    List<Order> getOrdersByStatus(Order.Status status);
    ResponseEntity<?> updateOrderStatus(Long orderId, Order.Status status);
}

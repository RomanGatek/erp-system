package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order getOrderById(Long id);

    List<Order> getOrders();

    List<Order> getOrdersByCostBetween(double start, double end);

    List<Order> getOrdersByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Order> getOrdersByProduct(Long productId);

    void createOrder(OrderDto orderDto);

    void updateOrder(Long id, Order order);

    void deleteOrder(Long id);

    void deleteOrderByProductId(Long productId);

}

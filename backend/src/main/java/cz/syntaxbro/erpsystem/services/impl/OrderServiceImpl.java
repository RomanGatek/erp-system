package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.services.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> orderOptional =  orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        }return null;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCostBetween(double start, double end) {
        return orderRepository.findByCostBetween(start, end);
    }

    @Override
    public List<Order> getOrdersByDateBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByDateBetween(start,end);
    }

    @Override
    public List<Order> getOrdersByProduct(Product product) {
        return orderRepository.findByProduct(product);
    }

    @Override
    public void createOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void updateOrder(Long id, Order order) {
        Optional<Order> OrderOptional = orderRepository.findById(id);
        if (OrderOptional.isPresent()) {
            orderRepository.save(order);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}

package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.repositories.OrderItemRepository;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.services.OrderItemService;
import cz.syntaxbro.erpsystem.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {


    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item with id %s not found " + id));
    }

    @Override
    public List<OrderItemReponse> getOrderItemsByOrderId(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        return order.map(value -> value.getOrderItems().stream()
                .map(OrderItemReponse::new)
                .collect(Collectors.toList()))
                .orElse(null);
    }

    @Override
    public void deleteOrderItemById(Long id) {
        if(getOrderItemById(id) != null) {
            orderItemRepository.deleteById(id);
        }
    }

    @Override
    public void createOrderItem(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }
}

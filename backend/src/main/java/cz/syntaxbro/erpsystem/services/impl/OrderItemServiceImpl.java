package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.repositories.OrderItemRepository;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.services.OrderItemService;
import cz.syntaxbro.erpsystem.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {


    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item with id %s not found " + id));
    }

    @Override
    public List<OrderItemReponse> getOrderItemsByOrderId(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if(order != null) {
            return order.getOrderItems().stream().map(OrderItemReponse::new).collect(Collectors.toList());
        }
        return null;
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

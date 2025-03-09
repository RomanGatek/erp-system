package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.exceptions.ResourceNotFoundException;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final OrderItemService orderItemService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, InventoryService inventoryService, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.orderItemService = orderItemService;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    @Override
    public List<OrderResponse> getOrders() {
        return orderRepository.findAll().stream().map(order -> {
            var orderResponse = new OrderResponse();
            orderResponse.map(order);
            orderResponse.setOrderItems(order.getOrderItems().stream().map(OrderItemReponse::new).toList());
            return orderResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
        return orderRepository.findByProduct(product);
    }

    @Override
    @Transactional
    public Order createdOrder(OrderCreateRequest orderRequest) {
        double totalCost = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .orderType(orderRequest.getOrderType())
                .approvedBy(this.getCurrentUser())
                .comment(orderRequest.getComment())
                .decisionTime(LocalDateTime.now())
                .status(Order.Status.PENDING)
                .cost(totalCost)
                .build();

        order = orderRepository.save(order);

        for (var orderItemRequest : orderRequest.getProducts()) {
            InventoryItem inventoryItem = inventoryService.getItem(orderItemRequest.getId());
            double productPrice = orderRequest.getOrderType() == Order.OrderType.SELL
                    ? inventoryItem.getProduct().getPurchasePrice()
                    : inventoryItem.getProduct().getBuyoutPrice();

            OrderItem orderItem = OrderItem.builder()
                    .inventoryItem(inventoryItem)
                    .quantity(orderItemRequest.getQuantity())
                    .order(order)
                    .build();

            orderItemService.createOrderItem(orderItem);
            orderItems.add(orderItem);
            totalCost += orderItemRequest.getQuantity() * productPrice;
        }

        order.setCost(totalCost);
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public void updateOrder(Long orderId, OrderRequest orderDto) {
        Order order = getOrderById(orderId);
        order.map(orderDto);
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    @Override
    public Order confirmOrder(Long id, String comment) {
        Order order = getOrderById(id);
        
        // Only pending orders can be confirmed
        if (!order.getStatus().equals(Order.Status.PENDING)) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }

        User user = getCurrentUser();

        for (var item : order.getOrderItems()) {
            if (item != null) inventoryService.releaseStock(item.getInventoryItem().getId(), item.getQuantity());
        }
        order.setStatus(Order.Status.CONFIRMED);
        order.setComment(comment);
        order.setDecisionTime(LocalDateTime.now());
        order.setApprovedBy(user);
        orderRepository.save(order);

        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id, String comment) {
        Order order = getOrderById(id);
        
        // Only pending orders can be canceled
        if (!order.getStatus().equals(Order.Status.PENDING)) {
            throw new IllegalStateException("Only pending orders can be canceled");
        }

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

            for (var item : order.getOrderItems()) {
                if (item != null) inventoryService.receiveStock(item.getInventoryItem().getId(), item.getQuantity());
            }
            order.setStatus(Order.Status.CANCELED);
            order.setComment(comment);
            order.setDecisionTime(LocalDateTime.now());
            order.setApprovedBy(user);
            orderRepository.save(order);

        return orderRepository.save(order);
    }

    @Override
    public Order addWorkflowComment(Long id, String comment) {
        Order order = getOrderById(id);
        order.setComment(comment);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public ResponseEntity<?> updateOrderStatus(Long orderId, Order.Status status){
        Order order = getOrderById(orderId);

        /*
        TODO... events/subcriber/observer
        když uživatel bude mít daný sub na dané objednávce, tak v rámci observeru dostane informaci o tom že status objednávky bude změnen.
         */

        return ResponseEntity.status(HttpStatus.OK).body("Order with id " + orderId + " has been updated to " + status);
    }

    private User getCurrentUser() {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
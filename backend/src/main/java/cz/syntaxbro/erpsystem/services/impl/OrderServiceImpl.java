package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.exceptions.ResourceNotFoundException;
import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.InventoryService;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.services.ProductService;
import cz.syntaxbro.erpsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
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
    public Order createdOrder(OrderCreateRequest orderRequest) {

        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .orderType(orderRequest.getOrderType())
                .comment(orderRequest.getComment())
                .approvedBy(getCurrentUser())
                .decisionTime(LocalDateTime.now())
                .build();
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
        
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        for (var item : order.getOrderItems()) {
            if (item != null) {
                if (order.getOrderType().equals(Order.OrderType.SELL)) {
                    inventoryService.releaseStock(item.getInventoryItem().getId(), item.getQuantity());
                } else {
                    inventoryService.receiveStock(item.getInventoryItem().getId(), item.getQuantity());
                }
            }
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
        User user = getCurrentUser();

            for (var item : order.getOrderItems()) {
                if (item != null) {
                    if (order.getOrderType().equals(Order.OrderType.SELL)) {
                        inventoryService.receiveStock(item.getInventoryItem().getId(), item.getQuantity());
                    } else {
                        inventoryService.releaseStock(item.getInventoryItem().getId(), item.getQuantity());
                    }
                }
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
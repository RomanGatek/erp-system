package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.exceptions.ResourceNotFoundException;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.requests.InventoryItemRequest;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.OrderUpdateRequest;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.*;
import cz.syntaxbro.erpsystem.utils.ConsoleColors;
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
import java.util.Optional;
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
            var orderResponse = OrderResponse.builder().build();
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
                .decisionTime(null)
                .status(Order.Status.PENDING)
                .cost(totalCost)
                .build();

        order = orderRepository.save(order);

        for (var orderItemRequest : orderRequest.getProducts()) {
            Product product = productRepository.findById(orderItemRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + orderItemRequest.getId() + " not found"));

            var optionalInventoryItem = inventoryService.findItemByProductForOrder(product);
            InventoryItem inventoryItem;

            inventoryItem = optionalInventoryItem.orElseGet(() -> inventoryService.addItem(InventoryItemRequest
                    .builder()
                    .stockedAmount(0d)
                    .productId(product.getId())
                    .build()
            ));

            double productPrice = orderRequest.getOrderType() == Order.OrderType.SELL
                    ? inventoryItem.getProduct().getPurchasePrice()
                    : inventoryItem.getProduct().getBuyoutPrice();

            Optional<OrderItem> existingOrderItem = orderItems.stream()
                    .filter(orderItem -> orderItem.getInventoryItem().getProduct().equals(product))
                    .findFirst();

            Order finalOrder = order;

            existingOrderItem.map(item -> {
                item.setQuantity(item.getQuantity() + orderItemRequest.getQuantity());
                return item;
            }).orElseGet(() -> {
                OrderItem newOrderItem = OrderItem.builder()
                        .inventoryItem(inventoryItem)
                        .quantity(orderItemRequest.getQuantity())
                        .order(finalOrder)
                        .build();

                orderItemService.createOrderItem(newOrderItem);
                orderItems.add(newOrderItem);

                return newOrderItem;
            });

            totalCost += orderItemRequest.getQuantity() * productPrice;
        }

        order.setCost(totalCost);
        order.setOrderItems(orderItems);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Long orderId, OrderUpdateRequest orderRequest) {
        Order order = getOrderById(orderId);

        if (!order.getStatus().equals(Order.Status.PENDING)) {
            throw new RuntimeException("You can edit only order with status PENDING.");
        }

        if (orderRequest.getOrderType() != null) {
            order.setOrderType(orderRequest.getOrderType());
        }

        if (orderRequest.getComment() != null) {
            order.setComment(orderRequest.getComment());
        }

        order.setUpdatedAt(LocalDateTime.now());

        order.setOrderItems(orderRequest.getProducts()
                .stream().map(product -> {
                    Product productFromDb = productRepository.findById(product.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product with id " + product.getId() + " not found"));

                    var optionalInventoryItem = inventoryService.findItemByProductForOrder(productFromDb);

                    InventoryItem inventoryItem;

                    if (optionalInventoryItem.isEmpty()) {
                        inventoryService.addItem(InventoryItemRequest.builder()
                                .stockedAmount(0)
                                .productId(productFromDb.getId())
                                .build());

                        inventoryItem = inventoryService.findItemByProduct(productFromDb);
                    } else {
                        inventoryItem = optionalInventoryItem.get();
                    }

                    return OrderItem.builder()
                            .order(order)
                            .inventoryItem(inventoryItem)
                            .quantity(product.getQuantity())
                            .build();
                }).toList()
        );

        return orderRepository.save(order);
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
            if (item != null) {
                if (order.getOrderType() == Order.OrderType.SELL) {
                    inventoryService.releaseStock(item.getInventoryItem().getId(), item.getQuantity());
                } else if (order.getOrderType() == Order.OrderType.PURCHASE) {
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
    public ResponseEntity<?> updateOrderStatus(Long orderId, Order.Status status) {
        Order order = getOrderById(orderId);

        /*
        TODO... events/subcriber/observer
        když uživatel bude mít daný sub na dané objednávce, tak v rámci observeru dostane informaci o tom že status objednávky bude změnen.
         */

        order.setStatus(status);

        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.OK).body("Order with id " + orderId + " has been updated to " + status);
    }

    public User getCurrentUser() {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
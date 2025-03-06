package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.exceptions.ResourceNotFoundException;
import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.services.InventoryService;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final InventoryService inventoryService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ProductService productService, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCostBetween(
            double start,
            double end) {
        if (start > end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cost must be grater or equal with 0");
        }
        return orderRepository.findByCostBetween(start, end);
    }

    @Override
    public List<Order> getOrdersByDateBetween(LocalDateTime start,
                                              LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater or equal than start");
        }
        return orderRepository.findByOrderTimeBetween(start, end);
    }

    @Override
    public List<Order> getOrdersByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
        return orderRepository.findByProduct(product);
    }

    @Override
    public Order createdOrder(Long itemId, int quantity) {
        Product product = productRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + itemId + " not found"));

        Order order = Order.builder()
                .product(product)
                .amount(quantity)
                .cost(product.getPrice() * quantity)
                .status(Order.Status.PENDING)
                .orderTime(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.Status.PENDING) {
            Long itemId = inventoryService.findItemByProduct(order.getProduct()).getId();
            inventoryService.releaseStock(itemId, order.getAmount());
            order.setStatus(Order.Status.CANCELED);
        }
    }


    @Override
    public void updateOrder(Long id, OrderRequest orderDto) {
        Order order = getOrderById(id);
        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + orderDto.getProductId() + " not found"));

        order.setProduct(product);
        order.setAmount(orderDto.getAmount());
        order.setCost(orderDto.getCost());
        order.setStatus(orderDto.getStatus());
        order.setOrderTime(orderDto.getOrderTime());
        order.setComment(orderDto.getComment());
        order.setApprovedBy(orderDto.getApprovedBy());
        order.setDecisionTime(orderDto.getDecisionTime());

        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    @Override
    public void deleteOrderByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
        orderRepository.deleteByProduct(product);
    }

    @Override
    public Order confirmOrder(Long id, String comment) {
        Order order = getOrderById(id);
        
        // Only pending orders can be confirmed
        if (order.getStatus() != Order.Status.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        order.setStatus(Order.Status.CONFIRMED);
        order.setComment(comment);
        order.setApprovedBy(username);
        order.setDecisionTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id, String comment) {
        Order order = getOrderById(id);
        
        // Only pending orders can be canceled
        if (order.getStatus() != Order.Status.PENDING) {
            throw new IllegalStateException("Only pending orders can be canceled");
        }
        
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        order.setStatus(Order.Status.CANCELED);
        order.setComment(comment);
        order.setApprovedBy(username);
        order.setDecisionTime(LocalDateTime.now());
        
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

    // Converts OrderDto to Order with exceptions.
    private Order mapToEntity(OrderRequest orderDto, Order order) {
        order.setAmount(orderDto.getAmount());
        setProduct(orderDto, order);
        order.setCost(orderDto.getCost());
        setStatus(orderDto, order);
        setOrderTime(orderDto, order);
        return order;
    }

    //Validate Status
    private void setStatus(OrderRequest orderDto, Order order){
        List<String> list = Arrays.stream(Order.Status.values())
                .map(Enum::name)
                .toList();
        if (!list.contains(orderDto.getStatus().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is not valid");
        }
        order.setStatus(orderDto.getStatus());
    }

    //Validate OrderTime
    private void setOrderTime(OrderRequest orderDto, Order order){
        if (orderDto.getOrderTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderTime cannot be day older than today");
        }
        order.setOrderTime(orderDto.getOrderTime());

    }

    //Validate Product
    private void setProduct(OrderRequest orderDto, Order order){
        Product product = createdById(orderDto.getProductId());
        order.setProduct(product);
    }

    //Product created by id exception
    private Product createdById(Long id) {
        if (!productService.isExistById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist");
        }
        return productService.getProductById(id);
    }
}
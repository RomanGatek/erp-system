package cz.syntaxbro.erpsystem.services.impl;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
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
        }return orderRepository.findByDateBetween(start,end);
    }

    @Override
    public List<Order> getOrdersByProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));
        return orderRepository.findByProduct(product);
    }

    @Override
    public Order createdOrder(Long itemId, int quantity) {
        if (!inventoryService.isStockAvailable(itemId, quantity)) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        inventoryService.reserveStock(itemId, quantity);

        InventoryItem item = inventoryService.getItem(itemId);
        double productCost = item.getProduct().getPrice();

        Order order = Order.builder()
                .cost(productCost * quantity)
                .product(item.getProduct())
                .orderTime(LocalDateTime.now())
                .amount(quantity)
                .status(Order.Status.PENDING)
                .build();

        orderRepository.save(order);

        return order;
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
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }
        Order order = orderOptional.get();
        Order mappedOrder = mapToEntity(orderDto, order);
        orderRepository.save(mappedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if(getOrderById(id) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }
        orderRepository.deleteById(id);

    }

    //delete all orders with witch include order
    @Override
    public void deleteOrderByProductId(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        List<Order> orders = orderRepository.findByProduct(product);
        if (orders.isEmpty() || product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found with product id " + productId);
        }
        int sumAmountOfOrders = orderRepository.sumAmountWithProduct(product);
        InventoryItem inventoryItem = inventoryService.findItemByProduct(product);
        inventoryService.receiveStock(inventoryItem.getId(), sumAmountOfOrders);
        orderRepository.deleteAll(orders);
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
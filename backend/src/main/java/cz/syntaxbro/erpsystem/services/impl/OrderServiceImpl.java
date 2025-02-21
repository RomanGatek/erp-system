package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> orderOptional =  orderRepository.findById(id);
        return orderOptional.orElse(null);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCostBetween(
            @NotEmpty @Min(value = 0, message = "Cost must be grater or equal with 0") double start,
            @NotEmpty @Min(value = 0, message = "Cost must be grater or equal with 0") double end) {
        if (start > end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cost must be grater or equal with 0");
        }
        return orderRepository.findByCostBetween(start, end);
    }

    @Override
    public List<Order> getOrdersByDateBetween(@NotEmpty @FutureOrPresent LocalDateTime start,
                                              @NotEmpty @FutureOrPresent LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater or equal than start");
        }return orderRepository.findByDateBetween(start,end);
    }

    @Override
    public List<Order> getOrdersByProduct(Long productId) {
        Product createdProduct = createdById(productId);
        return orderRepository.findByProduct(createdProduct);
    }

    @Override
    public Order createdOrder(OrderDto orderDto) {
        //map to OrderDto to Order
        Order createdOrder = mapToEntity(orderDto, new Order());
        orderRepository.save(createdOrder);
        return createdOrder;
    }

    @Override
    public void updateOrder(Long id, OrderDto orderDto) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            Order mappedOrder = mapToEntity(orderDto, order);
            orderRepository.save(mappedOrder);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");

    }

    @Override
    public void deleteOrder(Long id) {
        if(getOrderById(id) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }
        orderRepository.deleteById(id);

    }

    //delete all orders with witch include order
    @Override
    public void deleteOrderByProductId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found with product id " + productId);
        }
        Product product = productOptional.get();
        orderRepository.deleteAll(orderRepository.findByProduct(product));
    }



    // Converts OrderDto to Order with exceptions.
    private Order mapToEntity(OrderDto orderDto, Order order) {
        order.setAmount(orderDto.getAmount());
        setProduct(orderDto, order);
        order.setCost(orderDto.getCost());
        setStatus(orderDto, order);
        setOrderTime(orderDto, order);
        return order;
    }

    //Validate Status
    private void setStatus(OrderDto orderDto, Order order){
        List<String> list = Arrays.stream(Order.Status.values())
                .map(Enum::name)
                .toList();
        if (!list.contains(orderDto.getStatus().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is not valid");
        }
        order.setStatus(orderDto.getStatus());
    }

    //Validate OrderTime
    private void setOrderTime(OrderDto orderDto, Order order){
        if (orderDto.getOrderTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderTime cannot be day older than today");
        }
        order.setOrderTime(orderDto.getOrderTime());

    }

    //Validate Product
    private void setProduct(OrderDto orderDto, Order order){
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
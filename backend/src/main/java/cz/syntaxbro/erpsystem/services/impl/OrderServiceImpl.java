package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
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
import java.util.stream.Collectors;

@Service
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
    public List<Order> getOrdersByCostBetween(double start, double end) {
        if (start >= end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater than end");
        }else if (start < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start must be greater or equal to 0");
        }else if(end < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater or equal to 0");
        }else {
            return orderRepository.findByCostBetween(start, end);
        }
    }

    @Override
    public List<Order> getOrdersByDateBetween(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater or equal than start");
        }
        else if(orderRepository.findByDateBetween(start, end).isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No orders found");
        }else{
            return orderRepository.findByDateBetween(start,end);
        }
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
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Order updated");
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }
    }

    @Override
    public void deleteOrder(Long id) {
        if(orderRepository.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }else{
            orderRepository.deleteById(id);
        }
    }

    //delete all orders with witch include order
    @Override
    public void deleteOrderByProductId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            orderRepository.deleteAll(orderRepository.findByProduct(product));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found with product id " + productId);
        }

    }



    // Converts OrderDto to Order with exceptions.
    private Order mapToEntity(OrderDto orderDto, Order order) {
        setAmount(orderDto, order);
        setProduct(orderDto, order);
        setCost(orderDto, order);
        setStatus(orderDto, order);
        setOrderTime(orderDto, order);
        return order;
    }

    //Validate Amount
    private void setAmount(OrderDto orderDto, Order order){
        if(orderDto.getAmount() == null || orderDto.getAmount() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be null, must be greater than 0");
        }else {
            order.setAmount(orderDto.getAmount());
        }
    }

    //Validate Cost
    private void setCost(OrderDto orderDto, Order order){
        if(orderDto.getCost() == null || orderDto.getCost() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cost cannot be null, must be greater than 0");
        }else{
            order.setCost(orderDto.getCost());
        }
    }

    //Validate Status
    private void setStatus(OrderDto orderDto, Order order){
        List<String> list = Arrays.stream(Order.Status.values())
                .map(Enum::name)
                .toList();
        list.forEach(System.out::println);
        System.out.println(orderDto.getStatus().toString());
        if(orderDto.getStatus() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status cannot be null");
        } else if (!list.contains(orderDto.getStatus().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is not valid");
        } else {
            order.setStatus(orderDto.getStatus());
        }

    }

    //Validate OrderTime
    private void setOrderTime(OrderDto orderDto, Order order){
        if(orderDto.getOrderTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderTime cannot be null");
        } else if (orderDto.getOrderTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderTime cannot be day older than today");
        } else{
            order.setOrderTime(orderDto.getOrderTime());
        }
    }

    //Validate Product
    private void setProduct(OrderDto orderDto, Order order){
        if(orderDto.getProductId() == null || orderDto.getProductId() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductId cannot be null, must be greater than 0");
        }else{
            Product product = createdById(orderDto.getProductId());
            order.setProduct(product);
        }
    }

    //Product created by id exception
    private Product createdById(Long id) {
        if (!productService.isExistById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist");
        } else {
            return productService.getProductById(id);
        }
    }
}
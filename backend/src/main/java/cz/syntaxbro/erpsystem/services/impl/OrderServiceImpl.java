package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> orderOptional =  orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        }else{
            return null;
        }
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCostBetween(double start, double end) {
        if (start >= end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater than end");
        }else if (start < 0 || end < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and End must be greater than or equal to 0");
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
        Optional <Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return orderRepository.findByProduct(product);
        }return null;
    }

    @Override
    public Order createdOrder(OrderDto orderDto) {
        //map to OrderDto to Order
        Order createdOrder = mapToEntity(orderDto);
        orderRepository.save(createdOrder);
        return createdOrder;
    }

    @Override
    public void updateOrder(Long id, Order order) {
        Optional<Order> OrderOptional = orderRepository.findById(id);
        if (OrderOptional.isPresent()) {
            orderRepository.save(order);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No order found");
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    //delete all orders with witch include order
    @Override
    public void deleteOrderByProductId(Long productId) {
        Product product = productRepository.findById(productId).get();
        orderRepository.deleteAll(orderRepository.findByProduct(product));
    }

    // Converts OrderDto to Order with exceptions.
    private Order mapToEntity(OrderDto orderDto) {
        Order order = new Order();
        order.setAmount(orderDto.getAmount());
        order.setCost(orderDto.getCost());
        order.setStatus(orderDto.getStatus());
        order.setOrderTime(orderDto.getOrderTime());
        order.setProduct(createdById(orderDto.getProductId()));
        return order;
    }

    //Product created by id exception
    private Product createdById(Long id) {
        if(id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product must not be nul");
        } else if (!(id instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id must be Long");
        } else if (!productService.isExistById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist");
        } else {
            return productService.getProductById(id);
        }
    }
}

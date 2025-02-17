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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        order.setAmount(orderDto.getAmount());
        order.setCost(orderDto.getCost());
        order.setStatus(orderDto.getStatus());
        order.setOrderTime(orderDto.getOrderTime());
        order.setProduct(createdById(orderDto.getProductId()));
        return order;
    }

    //Product created by id exception
    private Product createdById(Long id) {
        if(id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product must not be null or less than 1");
        } else if (!productService.isExistById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist");
        } else {
            return productService.getProductById(id);
        }
    }
}

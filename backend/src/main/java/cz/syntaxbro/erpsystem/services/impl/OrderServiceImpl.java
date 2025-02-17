package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
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
        }return null;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCostBetween(double start, double end) {
        return orderRepository.findByCostBetween(start, end);
    }

    @Override
    public List<Order> getOrdersByDateBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByDateBetween(start,end);
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
    public void createOrder(Order order) {
        //validation if product exist
        if(productService.isExistById(order.getProduct().getId())) {
            orderRepository.save(order);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Product does not exist");
        }
    }

    @Override
    public void updateOrder(Long id, Order order) {
        Optional<Order> OrderOptional = orderRepository.findById(id);
        if (OrderOptional.isPresent()) {
            orderRepository.save(order);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    //delete all orders with witch include order
    @Override
    public void deleteOrderByProductId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Order> orders = orderRepository.findByProduct(product);
            orderRepository.deleteAll(orders);

        }
    }
}

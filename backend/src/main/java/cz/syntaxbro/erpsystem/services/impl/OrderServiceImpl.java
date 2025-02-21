package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.validates.OrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> orderOptional =  orderRepository.findById(id);
        return orderOptional.orElse(null);
    }

    @Override
    public Boolean existOrderById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        return orderOptional.isPresent();
    }

    @Override
    public Boolean existOrderByProductId(Long productId) {
        Optional <Product> productOptional = productRepository.findById(productId);
        return productOptional.isPresent();
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
    public Order createOrder(OrderRequest request) {
        Order newOrder = new Order();
        newOrder.setCost(request.getCost());
        newOrder.setAmount(request.getAmount());
        newOrder.setProduct(request.getProduct());
        newOrder.setOrderTime(request.getOrderTime());
        newOrder.setStatus(request.getStatus());
        return orderRepository.save(newOrder);

    }

    @Override
    public Order updateOrder(Long id, OrderRequest request) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setCost(request.getCost());
                    existingOrder.setAmount(request.getAmount());
                    existingOrder.setProduct(request.getProduct());
                    existingOrder.setOrderTime(request.getOrderTime());
                    existingOrder.setStatus(request.getStatus());
                    return orderRepository.save(existingOrder);
                }). orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

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

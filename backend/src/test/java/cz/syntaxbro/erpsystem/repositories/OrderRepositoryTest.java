package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Autowired
    OrderRepositoryTest(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setUp() {
        //Products
        Product productOne = new Product(null,"ProductOne", 100.0, 10);
        Product productTwo = new Product(null,"ProductTwo", 200.0, 10);
        //save products
        productRepository.save(productOne);
        productRepository.save(productTwo);

        // Date of orders
        LocalDateTime orderDateOne = LocalDateTime.of(2025, 2, 13, 10, 20);
        LocalDateTime orderDateTwo = LocalDateTime.of(2025, 2, 5, 9, 20);

        // Orders
        Order orderOne = new Order(null, productOne, 2, 200, Order.Status.ORDERED, orderDateOne);
        Order orderTwo = new Order(null, productTwo, 5, 1000, Order.Status.ORDERED, orderDateTwo);

        // Save Orders
        orderRepository.save(orderOne);
        orderRepository.save(orderTwo);
    }

    @Test
    void findByCostBetweenWithOneResult() {
        // Act
        List<Order> orders = orderRepository.findByCostBetween(100.0, 300.0);

        // Assert
        assertEquals(1, orders.size());
        assertEquals(200, orders.getFirst().getCost());
    }

    @Test
    void findByCostBetweenWithTwoResults() {
        // Act
        List<Order> orders = orderRepository.findByCostBetween(100.0, 1000.0);

        // Assert
        assertEquals(2, orders.size());
    }

    @Test
    void findByDateBetweenOrderDateWithOneResult() {
        //Act
        List<Order> orders = orderRepository.findByDateBetween(
                LocalDateTime.of(2025, 2, 7, 0, 0),
                LocalDateTime.of(2025, 2, 14, 0, 0));
        assertEquals(1, orders.size());
    }

    @Test
    void findByDateBetweenOrderDateWithTwoResult() {
        //Act
        List<Order> orders = orderRepository.findByDateBetween(
                LocalDateTime.of(2025, 2, 1, 0, 0),
                LocalDateTime.of(2025, 2, 25, 0, 0));
        assertEquals(2, orders.size());
    }

    @Test
    void findByProductOrderWithOneResult() {
        // Arrange
        Product product = productRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Act
        List<Order> orders = orderRepository.findByProduct(product);

        // Assert
        assertEquals(1, orders.size());
        assertEquals("ProductOne", orders.getFirst().getProduct().getName());
    }
}

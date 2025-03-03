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

@DataJpaTest // Runs tests with an embedded in-memory database (configures only JPA components)
class OrderRepositoryTest {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    OrderRepositoryTest(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    private Product productOne;

    /**
     * Sets up test data before each test execution.
     * - Creates and saves two products.
     * - Creates and saves two orders associated with those products.
     */
    @BeforeEach
    void setUp() {
        // Creating products
        productOne = new Product(null, "ProductOne", 100.0, "ProductOne");
        Product productTwo = new Product(null, "ProductTwo", 200.0, "ProductTwo");

        // Saving products and ensuring IDs are generated
        productOne = productRepository.save(productOne);
        productTwo = productRepository.save(productTwo);
        productRepository.flush(); // Forces Hibernate to assign IDs immediately

        // Order timestamps
        LocalDateTime orderDateOne = LocalDateTime.of(2025, 2, 13, 10, 20);
        LocalDateTime orderDateTwo = LocalDateTime.of(2025, 2, 5, 9, 20);

        // Creating orders linked to the products
        Order orderOne = new Order(null, productOne, 2, 200, Order.Status.ORDERED, orderDateOne);
        Order orderTwo = new Order(null, productTwo, 5, 1000, Order.Status.ORDERED, orderDateTwo);

        // Saving orders
        orderRepository.save(orderOne);
        orderRepository.save(orderTwo);
    }

    /**
     * Tests whether `findByCostBetween()` correctly retrieves a single order within the specified cost range.
     * Expected outcome:
     * - Only one order should be found.
     * - The cost of the retrieved order should be 200.
     */
    @Test
    void findByCostBetweenWithOneResult() {
        List<Order> orders = orderRepository.findByCostBetween(100.0, 300.0);
        assertEquals(1, orders.size());
        assertEquals(200, orders.getFirst().getCost());
    }

    /**
     * Tests whether `findByCostBetween()` correctly retrieves multiple orders within the specified cost range.
     * Expected outcome:
     * - Two orders should be found.
     */
    @Test
    void findByCostBetweenWithTwoResults() {
        List<Order> orders = orderRepository.findByCostBetween(100.0, 1000.0);
        assertEquals(2, orders.size());
    }

    /**
     * Tests whether `findByDateBetween()` correctly retrieves a single order within the specified date range.
     * Expected outcome:
     * - One order should be retrieved.
     */
    @Test
    void findByDateBetweenOrderDateWithOneResult() {
        List<Order> orders = orderRepository.findByDateBetween(
                LocalDateTime.of(2025, 2, 7, 0, 0),
                LocalDateTime.of(2025, 2, 14, 0, 0));
        assertEquals(1, orders.size());
    }

    /**
     * Tests whether `findByDateBetween()` correctly retrieves multiple orders within the specified date range.
     * Expected outcome:
     * - Two orders should be retrieved.
     */
    @Test
    void findByDateBetweenOrderDateWithTwoResults() {
        List<Order> orders = orderRepository.findByDateBetween(
                LocalDateTime.of(2025, 2, 1, 0, 0),
                LocalDateTime.of(2025, 2, 25, 0, 0));
        assertEquals(2, orders.size());
    }

    /**
     * Tests whether `findByProduct()` correctly retrieves an order associated with a specific product.
     * Expected outcome:
     * - One order should be retrieved.
     * - The retrieved order should be associated with "ProductOne".
     */
    @Test
    void findByProductOrderWithOneResult() {
        // Retrieving the product dynamically by its assigned ID
        Product product = productRepository.findById(productOne.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Fetch orders for the product
        List<Order> orders = orderRepository.findByProduct(product);

        // Assertions
        assertEquals(1, orders.size());
        assertEquals("ProductOne", orders.getFirst().getProduct().getName());
    }
}
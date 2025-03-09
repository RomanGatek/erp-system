package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.*;
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
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Autowired
    OrderRepositoryTest(OrderRepository orderRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    private Product productOne;
    private Product productTwo;
    private InventoryItem inventoryItemOne;
    private InventoryItem inventoryItemTwo;
    private OrderItem orderItemOne;
    private OrderItem orderItemTwo;


    /**
     * Sets up test data before each test execution.
     * - Creates and saves two products.
     * - Creates and saves two orders associated with those products.
     */
    @BeforeEach
    void setUp() {
        // Creating products
        productOne = new Product(null, "ProductOne", 100.0, 100.0, "ProductOne");
        productTwo = new Product(null, "ProductTwo", 200.0, 2000.0, "ProductTwo");
        productOne = productRepository.save(productOne);
        productTwo = productRepository.save(productTwo);

        // create inventory item from product
        inventoryItemOne = new InventoryItem(null, productOne, 100);
        inventoryItemTwo = new InventoryItem(null, productTwo, 200);
        inventoryItemOne = inventoryRepository.save(inventoryItemOne);
        inventoryItemTwo = inventoryRepository.save(inventoryItemTwo);

        // create order item from inventory item
        orderItemOne = new OrderItem().builder()
                .inventoryItem(inventoryItemOne)
                .quantity(10)
                .build();
        orderItemTwo = new OrderItem().builder()
                .inventoryItem(inventoryItemTwo)
                .quantity(10)
                .build();
        orderItemOne = orderItemRepository.save(orderItemOne);
        orderItemTwo = orderItemRepository.save(orderItemTwo);


        // Saving products and ensuring IDs are generated
        productRepository.flush(); // Forces Hibernate to assign IDs immediately
        inventoryRepository.flush();


        // Order timestamps
        LocalDateTime orderDateOne = LocalDateTime.of(2025, 2, 13, 10, 20);
        LocalDateTime orderDateTwo = LocalDateTime.of(2025, 2, 5, 9, 20);

        User user = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Creating orders linked to the products
        Order orderOne = Order.builder()
                .orderItems(List.of(orderItemOne, orderItemTwo))
                .orderTime(orderDateOne)
                .status(Order.Status.PENDING)
                .approvedBy(user)
                .orderType(Order.OrderType.SELL)
                .decisionTime(null)
                .cost(134.0)
                .build();
//        null, List.of(orderItemOne, orderItemTwo), 2, 200, Order.Status.PENDING, orderDateOne;
//        Order orderTwo = new Order(null, productTwo, 5, 1000, Order.Status.PENDING, orderDateTwo);

        // Saving orders
        orderRepository.save(orderOne);
//        orderRepository.save(orderTwo);
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
//    @Test
//    void findByDateBetweenOrderDateWithOneResult() {
//        List<Order> orders = orderRepository.findByDateBetween(
//                LocalDateTime.of(2025, 2, 7, 0, 0),
//                LocalDateTime.of(2025, 2, 14, 0, 0));
//        assertEquals(1, orders.size());
//    }

    /**
     * Tests whether `findByDateBetween()` correctly retrieves multiple orders within the specified date range.
     * Expected outcome:
     * - Two orders should be retrieved.
     */
//    @Test
//    void findByDateBetweenOrderDateWithTwoResults() {
//        List<Order> orders = orderRepository.findByDateBetween(
//                LocalDateTime.of(2025, 2, 1, 0, 0),
//                LocalDateTime.of(2025, 2, 25, 0, 0));
//        assertEquals(2, orders.size());
//    }

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
        assertEquals("ProductOne", orders.getFirst().getOrderItems().getFirst());
    }
}
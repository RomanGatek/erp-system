package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;

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

    /**
     * Sets up test data before each test execution.
     * - Creates and saves two products.
     * - Creates and saves two orders associated with those products.
     */
    @BeforeEach
    void setUp() {
        // Creating and saving a test user
        User testUser = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("!Password123")
                .firstName("Admin")
                .lastName("User")
                .active(true)
                .build();
        
        testUser = userRepository.save(testUser);
        
        // Verify user was saved
        assertNotNull(testUser.getId(), "User ID should be generated");

        // Creating products
        productOne = new Product(null, "ProductOne", 100.0, 100.0, "ProductOne");
        Product productTwo = new Product(null, "ProductTwo", 200.0, 2000.0, "ProductTwo");
        productOne = productRepository.save(productOne);
        productTwo = productRepository.save(productTwo);

        // Create inventory items from products
        InventoryItem inventoryItemOne = new InventoryItem(null, productOne, 100);
        InventoryItem inventoryItemTwo = new InventoryItem(null, productTwo, 200);
        inventoryItemOne = inventoryRepository.save(inventoryItemOne);
        inventoryItemTwo = inventoryRepository.save(inventoryItemTwo);

        // Create order items
        OrderItem orderItemOne = OrderItem.builder()
                .inventoryItem(inventoryItemOne)
                .quantity(10)
                .build();
        OrderItem orderItemTwo = OrderItem.builder()
                .inventoryItem(inventoryItemTwo)
                .quantity(10)
                .build();
        
        // Save order items
        orderItemOne = orderItemRepository.save(orderItemOne);
        orderItemTwo = orderItemRepository.save(orderItemTwo);

        // Force Hibernate to flush
        productRepository.flush();
        inventoryRepository.flush();
        orderItemRepository.flush();
        userRepository.flush();

        // Order timestamps
        LocalDateTime orderDateOne = LocalDateTime.of(2025, 2, 13, 10, 20);

        // Creating order linked to the test user and order items
        Order orderOne = Order.builder()
                .orderItems(List.of(orderItemOne, orderItemTwo))
                .orderTime(orderDateOne)
                .status(Order.Status.PENDING)
                .approvedBy(testUser)
                .orderType(Order.OrderType.SELL)
                .decisionTime(null)
                .cost(134.0)
                .build();

        // Saving order
        orderRepository.save(orderOne);
        orderRepository.flush();
        
        // Update order items with reference to the order
        orderItemOne.setOrder(orderOne);
        orderItemTwo.setOrder(orderOne);
        orderItemRepository.save(orderItemOne);
        orderItemRepository.save(orderItemTwo);
        orderItemRepository.flush();
    }

    /**
     * Tests whether `findByProduct()` correctly retrieves an order associated with a specific product.
     * Expected outcome:
     * - One order should be retrieved.
     * - The retrieved order should be associated with "ProductOne".
     */
    @Test
    @WithMockUser(roles = "ADMIN", username = "username")
    void findByProductOrderWithOneResult() {
        // Retrieving the product dynamically by its assigned ID
        Product product = productRepository.findById(productOne.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Fetch orders for the product
        List<Order> orders = orderRepository.findByProduct(product);

        // Assertions
        assertEquals(1, orders.size(), "Should find exactly one order for the product");
    }
}
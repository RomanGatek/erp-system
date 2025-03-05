package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    /**
     * Initializes mocks before each test execution.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test: Retrieves an order by ID.
     * Expected outcome:
     * - The response status should be `200 OK`.
     * - The response body should contain the expected order.
     */
    @Test
    public void testGetOrder() {

        // Arrange: Create a sample order
        Order order = new Order(1L, null, 5, 100.0, Order.Status.PENDING, LocalDateTime.now());
        when(orderService.getOrderById(1L)).thenReturn(order);

        // Act: Call the controller method
        ResponseEntity<Order> response = orderController.getOrder(1L);

        // Assert: Verify response
        assertEquals(200, response.getStatusCode().value());
        assertEquals(order, response.getBody());
    }

    /**
     * Test: Retrieves all orders.
     * Expected outcome:
     * - The response status should be `200 OK`.
     * - The response body should contain a list of orders.
     */
    @Test
    public void testGetAllOrders() {

        // Arrange: Create a sample list of orders
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.getOrders()).thenReturn(orders);

        // Act: Call the controller method
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Assert: Verify response
        assertEquals(200, response.getStatusCode().value());
        assertEquals(orders, response.getBody());
    }

    /**
     * Test: Creates a new order.
     * Expected outcome:
     * - The response status should be `200 OK`.
     * - The response body should contain the created order.
     */
    @Test
    public void testCreateOrder() {

        // Arrange
        Long itemId = 1L;
        int quantity = 5;
        Order createdOrder = Order.builder()
                .id(1L)
                .amount(quantity)
                .cost(100.0)
                .status(Order.Status.PENDING)
                .orderTime(LocalDateTime.now())
                .build();

        when(orderService.createdOrder(itemId, quantity)).thenReturn(createdOrder);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(itemId, quantity);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(createdOrder, response.getBody());
    }
} 
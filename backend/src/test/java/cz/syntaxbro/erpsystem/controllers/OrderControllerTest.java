package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
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

    private OrderResponse orderResponse;
    private Order order;

    /**
     * Initializes mocks before each test execution.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalDateTime now = LocalDateTime.now();
        // Arrange: Create a sample order
        this.orderResponse = OrderResponse.builder()
                .orderItems(List.of())
                .orderTime(now)
                .status(Order.Status.PENDING)
                .orderType(Order.OrderType.SELL)
                .build();


        this.order = Order.builder()
                .id(1L)
                .orderItems(List.of())
                .status(Order.Status.PENDING)
                .orderType(Order.OrderType.SELL)
                .orderTime(now)
                .build();
        when(orderService.getOrderById(1L)).thenReturn(order);
    }

    /**
     * Test: Retrieves an order by ID.
     * Expected outcome:
     * - The response status should be `200 OK`.
     * - The response body should contain the expected order.
     */
    @Test
    public void testGetOrder() {
        LocalDateTime now = LocalDateTime.now();
        // Arrange: Create a sample order
        OrderResponse orderResponse = OrderResponse.builder()
                .orderItems(List.of())
                .orderTime(now)
                .status(Order.Status.PENDING)
                .orderType(Order.OrderType.SELL)
                .build();


        Order order = Order.builder()
                .id(1L)
                .orderItems(List.of())
                .status(Order.Status.PENDING)
                .orderType(Order.OrderType.SELL)
                .orderTime(now)
                .build();
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
        List<OrderResponse> orders =List.of(this.orderResponse);
        when(orderService.getOrders()).thenReturn(orders);

        // Act: Call the controller method
        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders();

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
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .orderType(Order.OrderType.SELL)
                .comment("comment")
                .build();

        // Arrange
        Long itemId = 1L;
        int quantity = 5;
        Order createdOrder = this.order;

        when(orderService.createdOrder(orderCreateRequest)).thenReturn(createdOrder);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(orderCreateRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(createdOrder, response.getBody());
    }
} 
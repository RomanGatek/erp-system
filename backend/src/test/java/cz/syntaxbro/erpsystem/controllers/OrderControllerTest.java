package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrder() {
        Order order = new Order(1L, null, 5, 100.0, Order.Status.SHIPPED, LocalDateTime.now());
        when(orderService.getOrderById(1L)).thenReturn(order);

        ResponseEntity<Order> response = orderController.getOrder(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(order, response.getBody());
    }

    @Test
    public void testGetAllOrders() {
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.getOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testCreateOrder() {
        OrderRequest orderRequest = new OrderRequest(); // Předpokládáme, že máte třídu OrderRequest
        Order createdOrder = new Order(1L, null, 5, 100.0, Order.Status.SHIPPED, LocalDateTime.now());
        when(orderService.createdOrder(any(OrderRequest.class))).thenReturn(createdOrder);

        ResponseEntity<Order> response = orderController.createOrder(orderRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdOrder, response.getBody());
    }
} 
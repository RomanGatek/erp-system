package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.services.impl.OrderServiceImpl; // Předpokládáme, že máte implementaci
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Předpokládáme, že máte repozitář

    @Mock
    private ProductService productService; // Přidejte mock pro ProductService

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, null, 5, 100.0, Order.Status.SHIPPED, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);
        assertEquals(order, foundOrder);
    }

    @Test
    public void testGetOrders() {
        List<Order> orders = Collections.singletonList(new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrders();
        assertEquals(orders, foundOrders);
    }

    @Test
    public void testCreatedOrder() {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(5);
        orderRequest.setCost(100.0);
        orderRequest.setProductId(1L);
        orderRequest.setStatus(Order.Status.PROCESSING);
        orderRequest.setOrderTime(LocalDateTime.now().plusDays(2));

        Product product = new Product(1L, "New Product", 22.2, "Description");

        when(productService.isExistById(1L)).thenReturn(true);
        when(productService.getProductById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    savedOrder.setProduct(product);
                    return savedOrder;
                });

        Order result = orderService.createdOrder(orderRequest);

        assertEquals(1L, result.getId());
        assertEquals(product, result.getProduct());
        assertEquals(5, result.getAmount());
        assertEquals(100.0, result.getCost());
        assertEquals(Order.Status.PROCESSING, result.getStatus());
        assertEquals(orderRequest.getOrderTime(), result.getOrderTime());
    }

    @Test
    public void testUpdateOrderStatus() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order(orderId, null, 5, 100.0, Order.Status.PROCESSING, LocalDateTime.now());
        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

        // Act
        orderService.updateOrderStatus(orderId, Order.Status.SHIPPED);

        // Assert
        assertEquals(Order.Status.SHIPPED, order.getStatus()); // Ensure the status has been updated
        verify(orderRepository, times(1)).save(order); // Ensure that save was called once
    }

    @Test
    public void testUpdateOrderStatus_OrderNotFound() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            orderService.updateOrderStatus(orderId, Order.Status.SHIPPED);
        });
    }

} 
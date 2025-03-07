package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.services.impl.OrderServiceImpl; // Předpokládáme, že máte implementaci
// Přidejte import pro ProductService
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Předpokládáme, že máte repozitář

    @Mock
    private InventoryService inventoryService; // Přidejte mock pro InventoryService

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product testProduct;
    private InventoryItem testItem;
    private Order testOrder;
    private OrderRequest testOrderDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testProduct = new Product(1L, "Test Product", 50.0, "Test Product Description");
        testItem = new InventoryItem(1L, testProduct, 100);
        LocalDateTime now = LocalDateTime.now();
        testOrder = Order.builder()
                .id(1L)
                .product(testProduct)
                .amount(10)
                .cost(testProduct.getPrice() * 10)
                .status(Order.Status.PENDING)
                .orderTime(now)
                .build();
        this.testOrderDto = OrderRequest.builder()
                .productId(testOrder.getProduct().getId())
                .orderTime(now)
                .status(testOrder.getStatus())
                .amount(testOrder.getAmount())
                .cost(testOrder.getCost())
                .build();
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, null, 5, 100.0, Order.Status.PENDING, LocalDateTime.now());
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
        Long itemId = 1L;
        int quantity = 5;
        Product product = new Product(1L, "New Product", 22.2, "Description");
        InventoryItem inventoryItem = new InventoryItem(1L, product, 10);

        when(inventoryService.isStockAvailable(itemId, quantity)).thenReturn(true);
        when(inventoryService.getItem(itemId)).thenReturn(inventoryItem);
        doNothing().when(inventoryService).reserveStock(itemId, quantity);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    return savedOrder;
                });

        Order result = orderService.createdOrder(itemId, quantity);

        assertEquals(1L, result.getId());
        assertEquals(product, result.getProduct());
        assertEquals(quantity, result.getAmount());
        assertEquals(22.2 * quantity, result.getCost(), 0.01);
        assertEquals(Order.Status.PENDING, result.getStatus());
        assertNotNull(result.getOrderTime());

        verify(inventoryService, times(1)).isStockAvailable(itemId, quantity);
        verify(inventoryService, times(1)).reserveStock(itemId, quantity);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithSufficientStock() {
        Long itemId = 1L;
        int quantity = 5;
        Product product = new Product(1L, "New Product", 22.2, "Description");
        InventoryItem inventoryItem = new InventoryItem(1L, product, 10);

        when(inventoryService.isStockAvailable(itemId, quantity)).thenReturn(true);
        when(inventoryService.getItem(itemId)).thenReturn(inventoryItem);
        doNothing().when(inventoryService).reserveStock(itemId, quantity);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    return savedOrder;
                });

        Order result = orderService.createdOrder(itemId, quantity);

        assertEquals(1L, result.getId());
        assertEquals(product, result.getProduct());
        assertEquals(quantity, result.getAmount());
        assertEquals(22.2 * quantity, result.getCost(), 0.01);
        assertEquals(Order.Status.PENDING, result.getStatus());
        assertNotNull(result.getOrderTime());

        verify(inventoryService, times(1)).isStockAvailable(itemId, quantity);
        verify(inventoryService, times(1)).reserveStock(itemId, quantity);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithInsufficientStock() {
        Long itemId = 1L;
        int quantity = 200;

        when(inventoryService.isStockAvailable(itemId, quantity)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.createdOrder(itemId, quantity)
        );

        assertEquals("Not enough stock available", exception.getMessage());

        verify(inventoryService, times(0)).updateQuantity(itemId, quantity);
    }

    @Test
    void testCancelOrderSuccessfully() {
        Long orderId = 1L;
        int itemQuantityBeforeCanceled = this.testItem.getQuantity();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(inventoryService.findItemByProduct(testProduct)).thenReturn(testItem);
        doAnswer(invocation -> {
            testItem.setQuantity(testItem.getQuantity() + testOrder.getAmount());
            return null; // Because it's void
        }).when(inventoryService).receiveStock(testItem.getId(), testOrder.getAmount());

        orderService.cancelOrder(orderId);

        assertEquals(Order.Status.CANCELED, testOrder.getStatus());

        assertNotNull(testOrder.getOrderTime());
        assertEquals(testItem.getQuantity(), testOrder.getAmount() + itemQuantityBeforeCanceled);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void testCancelOrderThrowsExceptionWhenOrderNotFound() {
        Long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.cancelOrder(orderId)
        );

        assertEquals("Order not found", exception.getMessage());

        verify(inventoryService, times(0)).releaseStock(anyLong(), anyInt());
    }

    @Test
    void testCancelOrderDoesNotChangeStatusWhenOrderIsNotPending() {
        testOrder.setStatus(Order.Status.CONFIRMED); // Zmena stavu na COMPLETED
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        orderService.cancelOrder(orderId);

        assertEquals(Order.Status.CONFIRMED, testOrder.getStatus()); // Stav objednávky zostáva nezmenený
        verify(inventoryService, times(0)).releaseStock(anyLong(), anyInt()); // Metóda releaseStock nebola volaná
    }

    @Test
    void updateOrder_updateOrderWhenItemNotFound() {
        //Arrest
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderService.updateOrder(orderId, this.testOrderDto);
        });
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No order found", exception.getReason());
    }
} 
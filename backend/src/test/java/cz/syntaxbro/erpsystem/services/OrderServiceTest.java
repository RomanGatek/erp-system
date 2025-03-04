package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.services.impl.OrderServiceImpl; // Předpokládáme, že máte implementaci
// Přidejte import pro ProductService
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product testProduct;
    private InventoryItem testItem;
    private Order testOrder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testProduct = new Product(1L, "Test Product", 50.0, "Test Product Description");
        testItem = new InventoryItem(1L, testProduct, 100);
        testOrder = Order.builder()
                .id(1L)
                .product(testProduct)
                .amount(10)
                .cost(testProduct.getPrice() * 10)
                .status(Order.Status.PENDING)
                .orderTime(LocalDateTime.now())
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
        doNothing().when(inventoryService).updateQuantity(itemId, quantity); // <- Oprava pre void metódu
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    savedOrder.setId(1L);
                    return savedOrder;
                });

        Order result = orderService.createdOrder(itemId, quantity);

        assertEquals(1L, result.getId());
        assertEquals(product, result.getProduct());
        assertEquals(5, result.getAmount());
        assertEquals(22.2 * 5, result.getCost()); // Dynamicky vypočítaná cena
        assertEquals(Order.Status.PENDING, result.getStatus()); // Status podľa implementácie
        assertNotNull(result.getOrderTime());

        verify(inventoryService, times(1)).updateQuantity(itemId, quantity);
    }

    @Test
    void testCreateOrderWithSufficientStock() {
        Long itemId = 1L;
        int quantity = 5;

        when(inventoryService.isStockAvailable(itemId, quantity)).thenReturn(true);
        when(inventoryService.getItem(itemId)).thenReturn(testItem);

        Order mockOrder = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order createdOrder = orderService.createdOrder(itemId, quantity);

        assertNotNull(createdOrder);
        assertEquals(testProduct, createdOrder.getProduct());
        assertEquals(quantity, createdOrder.getAmount());
        assertEquals(testProduct.getPrice() * quantity, createdOrder.getCost());
        assertEquals(Order.Status.PENDING, createdOrder.getStatus());

        verify(inventoryService, times(1)).isStockAvailable(itemId, quantity);
        verify(inventoryService, times(1)).updateQuantity(itemId, quantity);
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

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(inventoryService.findItemByProduct(testProduct)).thenReturn(testItem);

        orderService.cancelOrder(orderId);

        assertEquals(Order.Status.CANCELED, testOrder.getStatus());

        verify(inventoryService, times(1)).releaseStock(testItem.getId(), testOrder.getAmount());
        verify(orderRepository, times(1)).findById(orderId);
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
} 
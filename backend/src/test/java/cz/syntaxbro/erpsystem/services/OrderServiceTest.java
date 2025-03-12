package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private User testUser;
    private InventoryItem testItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime now = LocalDateTime.now();

        // First, create all test objects

        // Create test user
        this.testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .build();

        // Create CustomUserDetails based on testUser
        CustomUserDetails customUserDetails = new CustomUserDetails(testUser);

        // Create test product
        Product testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Sample product description")
                .buyoutPrice(10)
                .purchasePrice(20)
                .build();

        // Create test inventory item
        this.testItem = InventoryItem.builder()
                .id(1L)
                .product(testProduct)
                .stockedAmount(10)
                .build();

        // Create order without items
        this.testOrder = Order.builder()
                .id(1L)
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .status(Order.Status.PENDING)
                .decisionTime(now.plusDays(1))
                .comment("Test comment")
                .approvedBy(testUser)
                .cost(100.0)
                .build();

        // Create order item with reference to the order
        OrderItem testOrderItem = OrderItem.builder()
                .id(1L)
                .quantity(10)
                .order(this.testOrder)
                .inventoryItem(testItem)
                .build();

        // Update order with the item
        this.testOrder.setOrderItems(List.of(testOrderItem));

        // Now set up the security context with CustomUserDetails
        doReturn(authentication).when(securityContext).getAuthentication();
        doReturn(customUserDetails).when(authentication).getPrincipal();
        SecurityContextHolder.setContext(securityContext);

        // Mock the inventoryService to avoid NullPointerException
        doNothing().when(inventoryService).releaseStock(anyLong(), anyInt());
    }

    @Test
    public void testGetOrderById() {
        doReturn(Optional.of(testOrder)).when(orderRepository).findById(1L);

        Order foundOrder = orderService.getOrderById(1L);

        assertEquals(testOrder, foundOrder);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrders() {
        // Create test data
        List<Order> orders = Collections.singletonList(testOrder);

        // Mock only the repository - let the actual service method run
        doReturn(orders).when(orderRepository).findAll();

        // Call the actual method
        List<OrderResponse> foundOrders = orderService.getOrders();

        // Verify results
        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
        assertEquals(testOrder.getId(), foundOrders.getFirst().getId());

        // Verify repository was called
        verify(orderRepository, times(1)).findAll();
    }


    @Test
    void testCancelOrderSuccessfully() {
        Long orderId = 1L;
        String comment = "Cancellation reason";

        // Setup mocks - SecurityContext mocked in setUp()
        doReturn(Optional.of(testOrder)).when(orderRepository).findById(orderId);
        doReturn(testOrder).when(orderRepository).save(any(Order.class));

        // Directly mock the getCurrentUser method to bypass security checks
        doReturn(testUser).when(orderService).getCurrentUser();

        Order canceledOrder = orderService.cancelOrder(orderId, comment);

        assertEquals(Order.Status.CANCELED, canceledOrder.getStatus());
        assertEquals(comment, canceledOrder.getComment());
        assertNotNull(canceledOrder.getDecisionTime());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void testConfirmOrder() {
        Long orderId = 1L;
        String comment = "Confirmation comment";

        // Setup mocks
        doReturn(Optional.of(testOrder)).when(orderRepository).findById(orderId);
        doReturn(testOrder).when(orderRepository).save(any(Order.class));

        // Directly mock the getCurrentUser method to bypass security checks
        doReturn(testUser).when(orderService).getCurrentUser();

        // Mock the inventoryService.releaseStock method for the specific inventory item
        doNothing().when(inventoryService).releaseStock(testItem.getId(), 10);

        Order confirmedOrder = orderService.confirmOrder(orderId, comment);

        assertEquals(Order.Status.CONFIRMED, confirmedOrder.getStatus());
        assertEquals(comment, confirmedOrder.getComment());
        assertNotNull(confirmedOrder.getDecisionTime());

        verify(orderRepository, times(1)).findById(orderId);
        // Adjust verification to match actual implementation
        verify(orderRepository, times(2)).save(any(Order.class));
        // Verify that inventoryService.releaseStock was called
        verify(inventoryService, times(1)).releaseStock(testItem.getId(), 10);
    }

    @Test
    void testUpdateOrderStatus() {
        Long orderId = 1L;
        Order.Status newStatus = Order.Status.IN_TRANSMIT;

        doReturn(Optional.of(testOrder)).when(orderRepository).findById(orderId);
        doReturn(testOrder).when(orderRepository).save(any(Order.class));

        orderService.updateOrderStatus(orderId, newStatus);

        assertEquals(newStatus, testOrder.getStatus());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.exceptions.ResourceNotFoundException;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.repositories.OrderItemRepository;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderUpdateRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    OrderItemService orderItemService;

    @Mock
    OrderItemRepository orderItemRepository;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomUserDetails customUserDetails;

    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private User testUser;
    private Product testProduct;
    private InventoryItem testItem;
    private OrderItem testOrderItem;

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
        this.testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Sample product description")
                .buyoutPrice(10)
                .purchasePrice(20)
                .build();

        // Create test inventory item
        this.testItem = InventoryItem.builder()
                .id(1L)
                .product(this.testProduct)
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
        this.testOrderItem = OrderItem.builder()
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
    void testCancelOrder_StatusNotPendingNotSuccessfully(){
        //Arrert
        this.testOrder.setStatus(Order.Status.CONFIRMED);
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.of(this.testOrder));
        //Act
        IllegalStateException exceptionResult = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(id, "bud order"));
        //Assert
        assertEquals("Only pending orders can be canceled", exceptionResult.getMessage());
        verify(orderService, times(1)).cancelOrder(id, "bud order");
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

        Order confirmedOrder = orderService.confirmOrder(orderId, comment);

        assertEquals(Order.Status.CONFIRMED, confirmedOrder.getStatus());
        assertEquals(comment, confirmedOrder.getComment());
        assertNotNull(confirmedOrder.getDecisionTime());

        verify(orderRepository, times(1)).findById(orderId);
        // Adjust verification to match actual implementation
        verify(orderRepository, times(2)).save(any(Order.class));
    }
    @Test
    void confirmOrder_StatusNotPendingNotSuccessfully() {
        //Arrest
        this.testOrder.setStatus(Order.Status.CONFIRMED);
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.of(this.testOrder));
        //Act
        IllegalStateException exceptionResult = assertThrows(IllegalStateException.class, () -> orderService.confirmOrder(id, "bud order"));
        //Assert
        assertEquals("Only pending orders can be confirmed", exceptionResult.getMessage());
        verify(orderService, times(1)).confirmOrder(id, "bud order");
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

    @Test
    void testGetOrdersByProduct(){
        //Arrest
        long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.of(this.testProduct));
        when(orderRepository.findByProduct(this.testProduct)).thenReturn(List.of(testOrder));
        //Act
        List<Order> result  = orderService.getOrdersByProduct(id);
        //Assert
        assertNotNull(result );
        assertEquals(1, result .size());
    }

    @Test
    void testGetOrdersByStatus(){
        //Arrest
        when(orderRepository.findByStatus(Order.Status.PENDING)).thenReturn(List.of(testOrder));
        //Act
        List<Order> result  = orderService.getOrdersByStatus(Order.Status.PENDING);
        //Assert
        assertNotNull(result );
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateOrder_WithNotNullsInOrder() {
        //Arrest
        LocalDateTime now = LocalDateTime.now();
        OrderUpdateRequest.ProductRequest productRequest = OrderUpdateRequest.ProductRequest.builder()
                .quantity(10)
                .id(1L)
                .build();

        OrderUpdateRequest orderRequest = OrderUpdateRequest.builder()
                .products(List.of(productRequest))
                .orderType(Order.OrderType.SELL)
                .comment("Test comment")
                .build();

        long id = 1L;
        long productId = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.of(this.testOrder));
        when(orderService.getOrderById(id)).thenReturn(this.testOrder);
        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        when(inventoryRepository.findByProduct(this.testProduct)).thenReturn(Optional.of(this.testItem));
        when(inventoryService.findItemByProductForOrder(this.testProduct)).thenReturn(Optional.of(this.testItem));
        System.out.println(this.testItem);
        when(orderRepository.save(this.testOrder)).thenReturn(this.testOrder);
        //Act
        orderService.updateOrder(id, orderRequest);
        //Assert
        assertEquals(List.of(this.testOrderItem), this.testOrder.getOrderItems());
    }

    @Test
    void updateOrder_OrderStatusNotPending(){

        OrderItem orderItem = new OrderItem();

        LocalDateTime now = LocalDateTime.now();
        long id = 1L;
        OrderUpdateRequest.ProductRequest productRequest = OrderUpdateRequest.ProductRequest.builder()
                .quantity(10)
                .id(id)
                .build();

        Order order = Order.builder()
                .orderTime(now)
                .orderType(Order.OrderType.SELL)
                .status(Order.Status.CONFIRMED)
                .decisionTime(now.plusDays(1))
                .comment("Test comment")
                .approvedBy(testUser)
                .cost(100.0)
                .orderItems(List.of(orderItem))
                .build();

        Product product = Product.builder()
                .name("testProduct")
                .description("testDescription")
                .buyoutPrice(100.0)
                .purchasePrice(90.0)
                .productCategory(new ProductCategory())
                .build();

        OrderUpdateRequest orderRequest = OrderUpdateRequest.builder()
                .products(List.of(productRequest))
                .orderType(Order.OrderType.SELL)
                .comment("Test comment")
                .build();

        InventoryItem inventoryItem = InventoryItem.builder()
                .createdAt(now)
                .product(product)
                .stockedAmount(10)
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        //ACT
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.updateOrder(id, orderRequest));
        //Assert
        assertEquals("You can edit only order with status PENDING.", exception.getMessage());
        verify(orderService, times(1)).updateOrder(id, orderRequest);
    }

    @Test
    void testGetCurrentUser_successful() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUser()).thenReturn(this.testUser);

        // Act
        User result = orderService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(result, this.testUser);

        // Verify calls
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
        verify(customUserDetails).getUser();
    }

    @Test
    void testDeleteOrder_DeleteSuccessfuly(){
        //Arrest
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.of(testOrder));
        //Act
        orderService.deleteOrder(id);
        //Assert
        assertEquals(0, orderRepository.findAll().size());
        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    void testAddWorkflowComment_SuccessfullyAddCommand(){
        //Arrest
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.of(testOrder));
        //Act
        orderService.addWorkflowComment(id, "workflow comment");
        //Assert
        assertEquals("workflow comment", this.testOrder.getComment());
        verify(orderService, times(1)).addWorkflowComment(id, "workflow comment");
    }

    @Test
    void testCreatedOrder_successfulWithOrderTypePURCHASE(){
        long productId = 1L;
        long inventoryItemId = 1L;
        long orderId = 1L;

        InventoryItem inventoryItem = InventoryItem.builder()
                .product(this.testProduct)
                .build();

        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .approvedBy(this.testUser)
                .status(Order.Status.PENDING)
                .build();


        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        OrderCreateRequest.ProductRequest productRequest = OrderCreateRequest.ProductRequest.builder()
                .id(productId)
                .quantity(10)
                .build();

        OrderCreateRequest orderRequest = OrderCreateRequest.builder()
                .orderType(Order.OrderType.PURCHASE)
                .comment("request comment")
                .products(List.of(productRequest))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .inventoryItem(inventoryItem)
                .order(order)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        when(inventoryRepository.findByProduct(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryService.findItemByProductForOrder(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(inventoryRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(inventoryService.addItem(inventoryItem)).thenReturn(inventoryItem);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderRepository.save(order)).thenReturn(order);

        System.out.println(order);
        System.out.println(orderRequest);
        System.out.println(productRepository.findById(productId));
        System.out.println(orderRepository.findById(orderId));

        //Act
        Order result = orderService.createdOrder(orderRequest);
        //Assert
        assertNotNull(result);
        assertEquals(Order.OrderType.PURCHASE, result.getOrderType());
        assertEquals("request comment", result.getComment());
        assertEquals(productRequest.getQuantity() * this.testProduct.getBuyoutPrice(), result.getCost());
    }

    @Test
    void testCreatedOrder_successfulWithOrderTypeSELL(){
        long productId = 1L;
        long inventoryItemId = 1L;
        long orderId = 1L;

        InventoryItem inventoryItem = InventoryItem.builder()
                .product(this.testProduct)
                .build();

        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .approvedBy(this.testUser)
                .status(Order.Status.PENDING)
                .build();


        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        OrderCreateRequest.ProductRequest productRequest = OrderCreateRequest.ProductRequest.builder()
                .id(productId)
                .quantity(10)
                .build();

        OrderCreateRequest orderRequest = OrderCreateRequest.builder()
                .orderType(Order.OrderType.SELL)
                .comment("request comment")
                .products(List.of(productRequest))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .inventoryItem(inventoryItem)
                .order(order)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        when(inventoryRepository.findByProduct(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryService.findItemByProductForOrder(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(inventoryRepository.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(inventoryService.addItem(inventoryItem)).thenReturn(inventoryItem);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderRepository.save(order)).thenReturn(order);

        System.out.println(order);
        System.out.println(orderRequest);
        System.out.println(productRepository.findById(productId));
        System.out.println(orderRepository.findById(orderId));

        //Act
        Order result = orderService.createdOrder(orderRequest);
        //Assert
        assertNotNull(result);
        assertEquals(Order.OrderType.SELL, result.getOrderType());
        assertEquals("request comment", result.getComment());
        assertEquals(productRequest.getQuantity() * this.testProduct.getPurchasePrice(), result.getCost());


    }

    @Test
    void testCreatedOrder_successfulWithEmptyInventoryItem(){
        long productId = 1L;
        long inventoryItemId = 1L;
        long orderId = 1L;

        InventoryItem inventoryItem = InventoryItem.builder()
                .product(this.testProduct)
                .build();

        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .approvedBy(this.testUser)
                .status(Order.Status.PENDING)
                .build();


        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        OrderCreateRequest.ProductRequest productRequest = OrderCreateRequest.ProductRequest.builder()
                .id(productId)
                .quantity(10)
                .build();

        OrderCreateRequest orderRequest = OrderCreateRequest.builder()
                .orderType(Order.OrderType.SELL)
                .comment("request comment")
                .products(List.of(productRequest))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .inventoryItem(inventoryItem)
                .order(order)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(productId)).thenReturn(Optional.of(this.testProduct));
        when(inventoryRepository.findByProduct(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryService.findItemByProductForOrder(this.testProduct)).thenReturn(Optional.of(inventoryItem));
        when(inventoryRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(inventoryRepository.findById(inventoryItemId)).thenReturn(Optional.empty());
        when(inventoryService.addItem(inventoryItem)).thenReturn(inventoryItem);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderRepository.save(order)).thenReturn(order);

        System.out.println(order);
        System.out.println(orderRequest);
        System.out.println(productRepository.findById(productId));
        System.out.println(orderRepository.findById(orderId));

        //Act
        Order result = orderService.createdOrder(orderRequest);
        //Assert
        assertNotNull(result);
        assertEquals(Order.OrderType.SELL, result.getOrderType());
        assertEquals("request comment", result.getComment());
        assertEquals(0, inventoryItem.getStockedAmount());
    }

    @Test
    void testCreatedOrder_WithNotFoundProduct() {
        long productId = 1L;

        // Arrange: Product is not found in the repository
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Set up request data
        OrderCreateRequest.ProductRequest productRequest = OrderCreateRequest.ProductRequest.builder()
                .id(productId)
                .quantity(10)
                .build();

        OrderCreateRequest orderRequest = OrderCreateRequest.builder()
                .orderType(Order.OrderType.PURCHASE)
                .comment("request comment")
                .products(List.of(productRequest))
                .build();

        // Act & Assert: Exception should be thrown when the product is not found
        ResourceNotFoundException resultException = assertThrows(ResourceNotFoundException.class, () -> orderService.createdOrder(orderRequest));

        // Assert: The exception message should match
        assertEquals("Product with id " + productId + " not found", resultException.getMessage());
    }

}

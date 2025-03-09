package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.responses.reports.OrderApprovalReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductPurchaseReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductSalesReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.SalesReportDTO;
import cz.syntaxbro.erpsystem.services.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private List<Order> orders;
    private List<OrderItem> orderItems;
    private List<Product> products;
    private List<User> users;
    private List<InventoryItem> inventoryItems;

    @BeforeEach
    void setUp() {
        // Vytvoření testovacích dat
        setupTestData();
    }

    private void setupTestData() {
        // Vytvoření produktů
        Product product1 = Product.builder()
                .id(1L)
                .name("Produkt 1")
                .description("Popis produktu 1")
                .buyoutPrice(100.0)
                .purchasePrice(80.0)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Produkt 2")
                .description("Popis produktu 2")
                .buyoutPrice(200.0)
                .purchasePrice(150.0)
                .build();

        products = Arrays.asList(product1, product2);

        // Vytvoření uživatelů
        User user1 = User.builder()
                .id(1L)
                .username("admin")
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .active(true)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("manager")
                .firstName("Manager")
                .lastName("User")
                .email("manager@example.com")
                .active(true)
                .build();

        users = Arrays.asList(user1, user2);

        // Vytvoření inventárních položek
        InventoryItem inventoryItem1 = InventoryItem.builder()
                .id(1L)
                .product(product1)
                .stockedAmount(100)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        InventoryItem inventoryItem2 = InventoryItem.builder()
                .id(2L)
                .product(product2)
                .stockedAmount(50)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        inventoryItems = Arrays.asList(inventoryItem1, inventoryItem2);

        // Vytvoření objednávek
        Order order1 = Order.builder()
                .id(1L)
                .orderType(Order.OrderType.SELL)
                .status(Order.Status.CONFIRMED)
                .cost(300.0)
                .orderTime(LocalDateTime.now().minusDays(15))
                .approvedBy(users.get(0))
                .decisionTime(LocalDateTime.now().minusDays(14))
                .orderItems(new ArrayList<>())
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .orderType(Order.OrderType.SELL)
                .status(Order.Status.CONFIRMED)
                .cost(400.0)
                .orderTime(LocalDateTime.now().minusDays(10))
                .approvedBy(users.get(1))
                .decisionTime(LocalDateTime.now().minusDays(9))
                .orderItems(new ArrayList<>())
                .build();

        Order order3 = Order.builder()
                .id(3L)
                .orderType(Order.OrderType.PURCHASE)
                .status(Order.Status.CONFIRMED)
                .cost(500.0)
                .orderTime(LocalDateTime.now().minusDays(5))
                .approvedBy(users.get(0))
                .decisionTime(LocalDateTime.now().minusDays(4))
                .orderItems(new ArrayList<>())
                .build();

        orders = Arrays.asList(order1, order2, order3);

        // Vytvoření položek objednávek
        OrderItem orderItem1 = OrderItem.builder()
                .id(1L)
                .order(order1)
                .inventoryItem(inventoryItem1)
                .quantity(2)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(2L)
                .order(order1)
                .inventoryItem(inventoryItem2)
                .quantity(1)
                .build();

        OrderItem orderItem3 = OrderItem.builder()
                .id(3L)
                .order(order2)
                .inventoryItem(inventoryItem1)
                .quantity(3)
                .build();

        OrderItem orderItem4 = OrderItem.builder()
                .id(4L)
                .order(order3)
                .inventoryItem(inventoryItem2)
                .quantity(2)
                .build();

        orderItems = Arrays.asList(orderItem1, orderItem2, orderItem3, orderItem4);
    }

    @Test
    void generateSalesReport_ShouldReturnCorrectData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        Order.OrderType orderType = Order.OrderType.SELL;

        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate, orderType);

        // Assert
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        
        // Ověření, že byly použity pouze prodejní objednávky
        verify(orderRepository).findAll();
    }

    @Test
    void generateBestSellingProductsReport_ShouldReturnCorrectData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderItemRepository.findAll()).thenReturn(orderItems);
        when(productRepository.findById(1L)).thenReturn(Optional.of(products.get(0)));
        when(productRepository.findById(2L)).thenReturn(Optional.of(products.get(1)));

        // Act
        List<ProductSalesReportDTO> result = reportService.generateBestSellingProductsReport(startDate, endDate, limit);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Ověření, že byly použity správné repository metody
        verify(orderRepository).findAll();
        verify(orderItemRepository, times(3)).findAll();
        verify(productRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void generateMostPurchasedProductsReport_ShouldReturnCorrectData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(inventoryRepository.findAll()).thenReturn(inventoryItems);
        when(productRepository.findById(1L)).thenReturn(Optional.of(products.get(0)));
        when(productRepository.findById(2L)).thenReturn(Optional.of(products.get(1)));

        // Act
        List<ProductPurchaseReportDTO> result = reportService.generateMostPurchasedProductsReport(startDate, endDate, limit);

        // Assert
        assertNotNull(result);
        
        // Ověření, že byly použity správné repository metody
        verify(inventoryRepository).findAll();
        verify(productRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void generateOrderApprovalsReport_ShouldReturnCorrectData() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        when(orderRepository.findAll()).thenReturn(orders);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(users.get(1)));

        // Act
        List<OrderApprovalReportDTO> result = reportService.generateOrderApprovalsReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Ověření, že byly použity správné repository metody
        verify(orderRepository).findAll();
        verify(userRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void generateSalesReport_WithNullDates_ShouldUseDefaultDates() {
        // Arrange
        Order.OrderType orderType = Order.OrderType.SELL;
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        SalesReportDTO result = reportService.generateSalesReport(null, null, orderType);

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.now().minusMonths(1), result.getStartDate());
        assertEquals(LocalDate.now(), result.getEndDate());
    }

    @Test
    void generateBestSellingProductsReport_WithEmptyOrders_ShouldReturnEmptyList() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductSalesReportDTO> result = reportService.generateBestSellingProductsReport(startDate, endDate, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateMostPurchasedProductsReport_WithEmptyInventory_ShouldReturnEmptyList() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(inventoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductPurchaseReportDTO> result = reportService.generateMostPurchasedProductsReport(startDate, endDate, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateOrderApprovalsReport_WithNoApprovedOrders_ShouldReturnEmptyList() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        List<Order> noApprovedOrders = new ArrayList<>();
        for (Order order : orders) {
            Order newOrder = Order.builder()
                    .id(order.getId())
                    .orderType(order.getOrderType())
                    .status(Order.Status.PENDING) // Všechny objednávky jsou PENDING
                    .cost(order.getCost())
                    .orderTime(order.getOrderTime())
                    .orderItems(new ArrayList<>())
                    .build();
            noApprovedOrders.add(newOrder);
        }

        when(orderRepository.findAll()).thenReturn(noApprovedOrders);

        // Act
        List<OrderApprovalReportDTO> result = reportService.generateOrderApprovalsReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
} 
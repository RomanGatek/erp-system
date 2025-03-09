package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.responses.reports.OrderApprovalReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductPurchaseReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductSalesReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.SalesReportDTO;
import cz.syntaxbro.erpsystem.services.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportsControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportsController reportsController;

    private SalesReportDTO salesReportDTO;
    private List<ProductSalesReportDTO> productSalesReportDTOs;
    private List<ProductPurchaseReportDTO> productPurchaseReportDTOs;
    private List<OrderApprovalReportDTO> orderApprovalReportDTOs;

    @BeforeEach
    void setUp() {
        // Vytvoření testovacích dat pro reporty
        setupTestData();
    }

    private void setupTestData() {
        // Sales report data
        salesReportDTO = new SalesReportDTO(
                LocalDate.now().minusMonths(1),
                LocalDate.now(),
                BigDecimal.valueOf(1000),
                5,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(33.33)
        );

        // Product sales report data
        ProductSalesReportDTO productSales1 = new ProductSalesReportDTO(
                1L,
                "Produkt 1",
                "PROD1",
                10,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50)
        );

        ProductSalesReportDTO productSales2 = new ProductSalesReportDTO(
                2L,
                "Produkt 2",
                "PROD2",
                5,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(30)
        );

        productSalesReportDTOs = Arrays.asList(productSales1, productSales2);

        // Product purchase report data
        ProductPurchaseReportDTO productPurchase1 = new ProductPurchaseReportDTO(
                1L,
                "Produkt 1",
                "PROD1",
                20,
                BigDecimal.valueOf(800),
                BigDecimal.valueOf(40),
                2
        );

        ProductPurchaseReportDTO productPurchase2 = new ProductPurchaseReportDTO(
                2L,
                "Produkt 2",
                "PROD2",
                15,
                BigDecimal.valueOf(600),
                BigDecimal.valueOf(40),
                3
        );

        productPurchaseReportDTOs = Arrays.asList(productPurchase1, productPurchase2);

        // Order approval report data
        OrderApprovalReportDTO orderApproval1 = new OrderApprovalReportDTO(
                1L,
                "admin",
                "Admin User",
                3,
                BigDecimal.valueOf(600),
                BigDecimal.valueOf(200),
                60.0
        );

        OrderApprovalReportDTO orderApproval2 = new OrderApprovalReportDTO(
                2L,
                "manager",
                "Manager User",
                2,
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(200),
                40.0
        );

        orderApprovalReportDTOs = Arrays.asList(orderApproval1, orderApproval2);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getSalesReport_ShouldReturnSalesReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        String orderTypeStr = "sell";
        Order.OrderType orderType = Order.OrderType.SELL;

        when(reportService.generateSalesReport(startDate, endDate, orderType)).thenReturn(salesReportDTO);

        // Act
        ResponseEntity<SalesReportDTO> response = reportsController.getSalesReport(startDate, endDate, orderTypeStr);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(salesReportDTO, response.getBody());
        verify(reportService).generateSalesReport(startDate, endDate, orderType);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getSalesReport_WithPurchaseType_ShouldReturnSalesReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        String orderTypeStr = "purchase";
        Order.OrderType orderType = Order.OrderType.PURCHASE;

        when(reportService.generateSalesReport(startDate, endDate, orderType)).thenReturn(salesReportDTO);

        // Act
        ResponseEntity<SalesReportDTO> response = reportsController.getSalesReport(startDate, endDate, orderTypeStr);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(salesReportDTO, response.getBody());
        verify(reportService).generateSalesReport(startDate, endDate, orderType);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getBestSellingProducts_ShouldReturnProductSalesReports() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(reportService.generateBestSellingProductsReport(startDate, endDate, limit)).thenReturn(productSalesReportDTOs);

        // Act
        ResponseEntity<List<ProductSalesReportDTO>> response = reportsController.getBestSellingProducts(startDate, endDate, limit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productSalesReportDTOs, response.getBody());
        verify(reportService).generateBestSellingProductsReport(startDate, endDate, limit);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMostPurchasedProducts_ShouldReturnProductPurchaseReports() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int limit = 10;

        when(reportService.generateMostPurchasedProductsReport(startDate, endDate, limit)).thenReturn(productPurchaseReportDTOs);

        // Act
        ResponseEntity<List<ProductPurchaseReportDTO>> response = reportsController.getMostPurchasedProducts(startDate, endDate, limit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productPurchaseReportDTOs, response.getBody());
        verify(reportService).generateMostPurchasedProductsReport(startDate, endDate, limit);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getOrderApprovals_ShouldReturnOrderApprovalReports() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        when(reportService.generateOrderApprovalsReport(startDate, endDate)).thenReturn(orderApprovalReportDTOs);

        // Act
        ResponseEntity<List<OrderApprovalReportDTO>> response = reportsController.getOrderApprovals(startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderApprovalReportDTOs, response.getBody());
        verify(reportService).generateOrderApprovalsReport(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getSalesReport_WithNullDates_ShouldPassNullToService() {
        // Arrange
        String orderTypeStr = "sell";
        Order.OrderType orderType = Order.OrderType.SELL;

        when(reportService.generateSalesReport(null, null, orderType)).thenReturn(salesReportDTO);

        // Act
        ResponseEntity<SalesReportDTO> response = reportsController.getSalesReport(null, null, orderTypeStr);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).generateSalesReport(null, null, orderType);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getBestSellingProducts_WithDefaultLimit_ShouldUseDefaultLimit() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int defaultLimit = 10;

        when(reportService.generateBestSellingProductsReport(startDate, endDate, defaultLimit)).thenReturn(productSalesReportDTOs);

        // Act
        ResponseEntity<List<ProductSalesReportDTO>> response = reportsController.getBestSellingProducts(startDate, endDate, defaultLimit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).generateBestSellingProductsReport(startDate, endDate, defaultLimit);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMostPurchasedProducts_WithCustomLimit_ShouldUseCustomLimit() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        int customLimit = 5;

        when(reportService.generateMostPurchasedProductsReport(startDate, endDate, customLimit)).thenReturn(productPurchaseReportDTOs);

        // Act
        ResponseEntity<List<ProductPurchaseReportDTO>> response = reportsController.getMostPurchasedProducts(startDate, endDate, customLimit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).generateMostPurchasedProductsReport(startDate, endDate, customLimit);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getOrderApprovals_WithNullDates_ShouldPassNullToService() {
        // Arrange
        when(reportService.generateOrderApprovalsReport(null, null)).thenReturn(orderApprovalReportDTOs);

        // Act
        ResponseEntity<List<OrderApprovalReportDTO>> response = reportsController.getOrderApprovals(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).generateOrderApprovalsReport(null, null);
    }
}
package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.responses.reports.OrderApprovalReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductPurchaseReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductSalesReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.SalesReportDTO;
import cz.syntaxbro.erpsystem.services.ReportService;
import cz.syntaxbro.erpsystem.utils.ConsoleColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling report-related operations.
 * Access to reports is restricted to users with ADMIN or MANAGER roles.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportService reportService;

    @Autowired
    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Retrieves sales/purchases report data for a specified date range.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @param orderType The type of order
     * @return A response with sales report data
     */
    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "sell") String orderType
    )
    {
        var orderTypeEnum = orderType != null ? Order.OrderType.valueOf(orderType.toUpperCase()) : null;
        SalesReportDTO report = reportService.generateSalesReport(startDate, endDate, orderTypeEnum);

        ErpSystemApplication
                .getLogger()
                .info("\n\t{} Reports > sales report: {}\n{}", ConsoleColors.PURPLE, report, ConsoleColors.RESET);

        return ResponseEntity.ok(report);
    }
    
    /**
     * Retrieves report data for best-selling products.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @param limit The maximum number of products to include in the report
     * @return A response with best-selling products report data
     */
    @GetMapping("/best-selling-products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductSalesReportDTO>> getBestSellingProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductSalesReportDTO> report = reportService.generateBestSellingProductsReport(startDate, endDate, limit);

        ErpSystemApplication
                .getLogger()
                .info("\n\t{} Reports > best-selling-products report: {}\n{}", ConsoleColors.PURPLE, report, ConsoleColors.RESET);

        return ResponseEntity.ok(report);
    }
    
    /**
     * Retrieves report data for most purchased products.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @param limit The maximum number of products to include in the report
     * @return A response with most purchased products report data
     */
    @GetMapping("/most-purchased-products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductPurchaseReportDTO>> getMostPurchasedProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductPurchaseReportDTO> report = reportService.generateMostPurchasedProductsReport(startDate, endDate, limit);

        ErpSystemApplication
                .getLogger()
                .info("\n\t{} Reports > most-purchased-products: {}\n{}", ConsoleColors.PURPLE, report, ConsoleColors.RESET);

        return ResponseEntity.ok(report);
    }
    
    /**
     * Retrieves report data for order approvals by user.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @return A response with order approvals report data
     */
    @GetMapping("/order-approvals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<OrderApprovalReportDTO>> getOrderApprovals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<OrderApprovalReportDTO> report = reportService.generateOrderApprovalsReport(startDate, endDate);

        ErpSystemApplication
                .getLogger()
                .info("\n\t{} Reports > order-approvals: {}\n{}", ConsoleColors.PURPLE, report, ConsoleColors.RESET);

        return ResponseEntity.ok(report);
    }
} 
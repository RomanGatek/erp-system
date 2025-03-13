package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.responses.reports.OrderApprovalReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductPurchaseReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductSalesReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.SalesReportDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for generating various reports.
 */
public interface ReportService {
    
    /**
     * Generates a sales report for the specified date range.
     * 
     * @param startDate The start date for the report period (optional)
     * @param endDate The end date for the report period (optional)
     * @return A sales report DTO with aggregated sales data
     */
    SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate, Order.OrderType orderType);
    
    /**
     * Generates a report of best-selling products for the specified date range.
     * 
     * @param startDate The start date for the report period (optional)
     * @param endDate The end date for the report period (optional)
     * @param limit The maximum number of products to include in the report
     * @return A list of product sales report DTOs sorted by sales volume
     */
    List<ProductSalesReportDTO> generateBestSellingProductsReport(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Generates a report of most purchased products for the specified date range.
     * 
     * @param startDate The start date for the report period (optional)
     * @param endDate The end date for the report period (optional)
     * @param limit The maximum number of products to include in the report
     * @return A list of product purchase report DTOs sorted by purchase volume
     */
    List<ProductPurchaseReportDTO> generateMostPurchasedProductsReport(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Generates a report of order approvals by user for the specified date range.
     * 
     * @param startDate The start date for the report period (optional)
     * @param endDate The end date for the report period (optional)
     * @return A list of order approval report DTOs sorted by approval count
     */
    List<OrderApprovalReportDTO> generateOrderApprovalsReport(LocalDate startDate, LocalDate endDate);
} 
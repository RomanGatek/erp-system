package cz.syntaxbro.erpsystem.responses.reports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for sales report information.
 */
@Setter
@Getter
@ToString
public class SalesReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private int orderCount;
    private BigDecimal averageOrderValue;
    private BigDecimal dailyAverageSales;

    public SalesReportDTO() {
    }

    public SalesReportDTO(LocalDate startDate, LocalDate endDate, BigDecimal totalSales, 
                         int orderCount, BigDecimal averageOrderValue, BigDecimal dailyAverageSales) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSales = totalSales;
        this.orderCount = orderCount;
        this.averageOrderValue = averageOrderValue;
        this.dailyAverageSales = dailyAverageSales;
    }

}
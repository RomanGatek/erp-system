package cz.syntaxbro.erpsystem.responses.reports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product sales report information.
 */
@Setter
@Getter
@ToString
public class ProductSalesReportDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private int quantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal unitPrice;
    private BigDecimal percentageOfTotalSales;

    public ProductSalesReportDTO() {
    }

    public ProductSalesReportDTO(Long productId, String productName, String productCode, 
                                int quantitySold, BigDecimal totalRevenue, BigDecimal unitPrice,
                                BigDecimal percentageOfTotalSales) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
        this.unitPrice = unitPrice;
        this.percentageOfTotalSales = percentageOfTotalSales;
    }

}
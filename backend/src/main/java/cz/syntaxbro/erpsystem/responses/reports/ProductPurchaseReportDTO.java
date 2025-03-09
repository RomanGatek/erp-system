package cz.syntaxbro.erpsystem.responses.reports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product purchase report information.
 */
@Setter
@Getter
@ToString
public class ProductPurchaseReportDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private int quantityPurchased;
    private BigDecimal totalCost;
    private BigDecimal averagePurchasePrice;
    private int purchaseCount;

    public ProductPurchaseReportDTO() {
    }

    public ProductPurchaseReportDTO(Long productId, String productName, String productCode, 
                                   int quantityPurchased, BigDecimal totalCost, 
                                   BigDecimal averagePurchasePrice, int purchaseCount) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.quantityPurchased = quantityPurchased;
        this.totalCost = totalCost;
        this.averagePurchasePrice = averagePurchasePrice;
        this.purchaseCount = purchaseCount;
    }

}
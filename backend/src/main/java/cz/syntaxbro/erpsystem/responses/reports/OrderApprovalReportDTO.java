package cz.syntaxbro.erpsystem.responses.reports;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for order approval report information.
 */
@Setter
@Getter
@ToString
public class OrderApprovalReportDTO {
    private Long userId;
    private String username;
    private String fullName;
    private int approvedOrderCount;
    private BigDecimal totalApprovedAmount;
    private BigDecimal averageApprovedOrderAmount;
    private double percentageOfTotalApprovals;

    public OrderApprovalReportDTO() {
    }

    public OrderApprovalReportDTO(Long userId, String username, String fullName, 
                                 int approvedOrderCount, BigDecimal totalApprovedAmount,
                                 BigDecimal averageApprovedOrderAmount, double percentageOfTotalApprovals) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.approvedOrderCount = approvedOrderCount;
        this.totalApprovedAmount = totalApprovedAmount;
        this.averageApprovedOrderAmount = averageApprovedOrderAmount;
        this.percentageOfTotalApprovals = percentageOfTotalApprovals;
    }

}
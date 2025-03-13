package cz.syntaxbro.erpsystem.responses.reports;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class AllReportsDTO {
    private SalesReportDTO salesReport;
    private List<ProductSalesReportDTO> productSalesReports;
    private List<ProductPurchaseReportDTO> productPurchaseReports;
    private List<OrderApprovalReportDTO> orderApprovalReports;
}

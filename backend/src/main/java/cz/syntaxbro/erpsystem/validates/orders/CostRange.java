package cz.syntaxbro.erpsystem.validates.orders;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CostRange {

    @Min(value = 0, message = "Cost must be grater or equal with 0")
    private Double startCost;

    private Double endCost;

    public CostRange(Double start, Double end) {
        this.startCost = start;
        this.endCost = end;
    }

    @AssertTrue(message = "End cost must be greater than or equal to start cost")
    public boolean isValidRange() {
        return startCost != null && endCost != null && endCost >= startCost;
    }
}

package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Integer amount;

    @NotNull(message = "Cost cannot be null")
    @Positive(message = "Cost must be greater than 0")
    private Double cost;

    @NotNull(message = "Status cannot be null")
    private Order.Status status;

    @FutureOrPresent
    private LocalDateTime orderTime;

    @NotNull(message = "Status cannot be null")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private Long productId;
    
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
    
    private String approvedBy;
    
    private LocalDateTime decisionTime;
}

package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import cz.syntaxbro.erpsystem.models.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private Integer amount;

    @NotNull(message = "Cost cannot be null")
    @Positive(message = "Cost must be greater than 0")
    private Double cost;

    @NotNull(message = "Status cannot be null")
    private Order.Status status;

    @FutureOrPresent
    private LocalDateTime orderTime;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItem> orderItems;
    
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
    
    private User approvedBy;
    
    private LocalDateTime decisionTime;
}

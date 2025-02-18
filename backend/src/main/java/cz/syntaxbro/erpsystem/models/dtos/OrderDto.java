package cz.syntaxbro.erpsystem.models.dtos;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotBlank(message = "Amount is required")
    private int amount;
    @NotBlank(message = "Cost is required")
    private double cost;
    @NotBlank(message = "Status is required")
    @NonNull
    private Order.Status status;
    @NonNull
    private LocalDateTime orderTime;
    @NotBlank(message = "Product is required")
    @NonNull
    private Long productId;
}

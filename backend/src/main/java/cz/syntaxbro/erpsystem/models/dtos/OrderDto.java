package cz.syntaxbro.erpsystem.models.dtos;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Integer amount;

    @NotNull(message = "Cost cannot be null")
    @Positive(message = "Cost must be greater than 0")
    private Double cost;

    @NotNull(message = "Status cannot be null")
    private Order.Status status;

    @FutureOrPresent
    private LocalDateTime orderTime;

    @NotNull
    @Min(1)
    private Long productId;
}

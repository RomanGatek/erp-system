package cz.syntaxbro.erpsystem.models.dtos;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Integer amount;

    @NotBlank(message = "Cost cannot be null, or empty")
    @Size(min = 1, message = "Cost must be greater than 0")
    private Double cost;

    @NotBlank(message = "Status cannot be null or empty")
    private Order.Status status;

    @NotBlank(message = "Date cannot be null, must be greater than 0")
    @FutureOrPresent
    private LocalDateTime orderTime;

    @NotBlank
    @Min(1)
    private Long productId;
}

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

    @NotBlank
    @Size(min = 1, message = "Cost cannot be null, must be greater than 0")
    private Double cost;

    @NotBlank
    private Order.Status status;

    @NotBlank
    @FutureOrPresent
    private LocalDateTime orderTime;

    @NotBlank
    @Min(1)
    private Long productId;
}

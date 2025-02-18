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

    private Integer amount;

    private Double cost;

    private Order.Status status;

    private LocalDateTime orderTime;

    private Long productId;
}

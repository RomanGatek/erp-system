package cz.syntaxbro.erpsystem.models.dtos;

import cz.syntaxbro.erpsystem.models.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int amount;
    private double cost;
    private Order.Status status;
    private LocalDateTime orderTime;
    private Long productId;
}

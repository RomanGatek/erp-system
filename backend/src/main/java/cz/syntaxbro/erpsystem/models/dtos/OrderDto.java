package cz.syntaxbro.erpsystem.models.dtos;

import cz.syntaxbro.erpsystem.models.Order.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long productId;
    private int amount;
    private double cost;
    private Status status;
    private LocalDateTime orderTime;
}

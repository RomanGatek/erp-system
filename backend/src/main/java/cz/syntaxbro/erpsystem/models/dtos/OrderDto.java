package cz.syntaxbro.erpsystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {


    private Long id;


    private Long productId;


    private String productName;

    private int amount;

    private Double cost;

    private LocalDateTime orderTime;

    private String status;

}
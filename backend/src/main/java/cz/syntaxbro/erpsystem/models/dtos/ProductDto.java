package cz.syntaxbro.erpsystem.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Product cost must be greater than zero")
    private Double cost;

    @PositiveOrZero(message = "Product quantity must be zero or positive")
    private Integer quantity;
}

package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Product price must be greater than zero")
    private Double price;
    private String description;
}

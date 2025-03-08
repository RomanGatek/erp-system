package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Product price must not be null")
    @Positive(message = "Product price must be greater than zero")
    private double buyoutPrice;

    @NotNull(message = "Product price must not be null")
    @Positive(message = "Product price must be greater than zero")
    private double purchasePrice;

    private String description;
}

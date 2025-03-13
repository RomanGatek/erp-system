package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

    @NotNull(message = "Category can't be null")
    private String productCategory;
    private String image;
}

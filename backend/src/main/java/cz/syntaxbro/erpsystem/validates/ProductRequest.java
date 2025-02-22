package cz.syntaxbro.erpsystem.validates;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Username is required")
    public String name;

    @NotNull(message = "Cost is required")
    @Positive(message = "Product cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Cost must be a number")
    public double cost;

    @NotNull(message = "Quantity is required")
    @Min(value= 0, message = "Value must be min 0")
    public int quantity;
}

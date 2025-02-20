package cz.syntaxbro.erpsystem.validates;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Username is required")
    public String name;

    @NotBlank(message = "Cost is required")
    @Digits(integer = 10, fraction = 2, message = "Cost must be a number")
    public double cost;

    @NotBlank(message = "Quantity is required")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be a integer")
    public int quantity;
}

package cz.syntaxbro.erpsystem.validates;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    @Digits(integer = 10, fraction = 0, message = "Product ID must be an integer")
    @NotFound(action = NotFoundAction.EXCEPTION)
    public Product product;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 0, message = "Amount must be an integer")
    public int amount;

    @NotNull(message = "Cost is required")
    @Positive(message = "Cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Cost must be an integer")
    public Double cost;

    @NotBlank(message = "Status is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Pattern(regexp = "PREORDER|ORDERED|DONE",
            message = "Status must be one of: PREORDER, ORDERED, DONE")
    private Order.Status status;


    @PastOrPresent(message = "Order time cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Order time is required")
    private LocalDateTime orderTime;


}

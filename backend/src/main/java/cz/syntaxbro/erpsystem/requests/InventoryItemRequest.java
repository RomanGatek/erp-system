package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Validated
public class InventoryItemRequest {

    @NotNull(message = "Product cannot be empty")
    Long productId;

    @PositiveOrZero(message = "Quantity need to be 0 or more than")
    double stockedAmount;

    Long id;
}

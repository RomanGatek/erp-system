package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull(message = "OrderType cannot be null.")
    private Order.OrderType orderType;

    @NotNull(message = "OrderType cannot be null.")
    private String comment;

    List<ProductRequest> products;

    @Data
    static class ProductRequest {
        @NotNull(message = "OrderType cannot be null.")
        @Min(value = 1, message = "Id must be a positive number")
        private Long id;

        @NotNull(message = "OrderType cannot be null.")
        @Min(value = 1, message = "Quantity must be a positive number")
        private Integer quantity;
    }
}

package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderCreateRequest {

    @NotNull(message = "Order Type cannot be null")
    private Order.OrderType orderType;

    @NotNull(message = "Order Type cannot be null")
    private String comment;

    List<ProductRequest> products;

    @Data
    public static class ProductRequest {
        @Min(value = 1, message = "Id must be a number")
        @NotNull(message = "Id cannot be null")
        private Long id;

        @Min(value = 1, message = "Quantity must be a number")
        @NotNull(message = "Quantity cannot be null")
        private Integer quantity;
    }
}

package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderUpdateRequest {

    private Order.OrderType orderType;
    private String comment;
    private List<ProductRequest> products;

    @Data
    @Builder
    public static class ProductRequest {
        @Min(value = 1, message = "Id must be a number")
        @NotNull(message = "Id cannot be null")
        private Long id;

        @Min(value = 1, message = "Quantity must be a number")
        @NotNull(message = "Quantity cannot be null")
        private Integer quantity;
    }
}

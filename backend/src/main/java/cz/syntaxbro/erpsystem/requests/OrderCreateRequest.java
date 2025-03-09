package cz.syntaxbro.erpsystem.requests;

import cz.syntaxbro.erpsystem.models.Order;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    private Order.OrderType orderType;
    private String comment;

    List<ProductRequest> products;

    @Data
    static class ProductRequest {
        private Long id;
        private Integer quantity;
    }
}

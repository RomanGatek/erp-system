package cz.syntaxbro.erpsystem.responses;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    public void map(Order order) {
        this.id = order.getId();
        this.approvedBy = order.getApprovedBy();
        this.cost = order.getCost();
        this.comment = order.getComment();
        this.decisionTime = order.getDecisionTime();
        this.status = order.getStatus();
        this.orderType = order.getOrderType();
        this.orderTime = order.getOrderTime();
    }

    private Long id;

    private List<OrderItemReponse> orderItems;

    private double cost;

    private Order.Status status;

    private LocalDateTime orderTime;

    private String comment;

    private User approvedBy;

    private LocalDateTime decisionTime;

    private Order.OrderType orderType;
}

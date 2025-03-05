package cz.syntaxbro.erpsystem.events;

import cz.syntaxbro.erpsystem.models.Order;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderStatusChangedEvent {

    private final Long orderId;
    private final Order.Status status;

    @Autowired
    public OrderStatusChangedEvent(Long orderId, Order.Status status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Order.Status getStatus() {
        return status;
    }
}

package cz.syntaxbro.erpsystem.events;

import cz.syntaxbro.erpsystem.models.Order;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class OrderStatusChangedEvent {

    private final Long orderId;
    private final Order.Status status;

    @Autowired
    public OrderStatusChangedEvent(Long orderId, Order.Status status) {
        this.orderId = orderId;
        this.status = status;
    }

}

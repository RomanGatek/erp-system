package cz.syntaxbro.erpsystem.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        System.out.println("Order " + event.getOrderId() + " changed status to " + event.getStatus());
    }
}

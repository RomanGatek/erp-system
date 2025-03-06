package cz.syntaxbro.erpsystem.events;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    private final Logger logger;

    @Autowired
    public OrderStatusListener(Logger logger) {
        this.logger = logger;
    }

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        logger.info("Order {} changed status to {}", event.getOrderId(), event.getStatus());
    }
}

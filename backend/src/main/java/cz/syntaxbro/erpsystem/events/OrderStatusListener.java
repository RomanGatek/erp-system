package cz.syntaxbro.erpsystem.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusListener.class);

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        logger.info("Order {} changed status to {}", event.getOrderId(), event.getStatus());
    }
}

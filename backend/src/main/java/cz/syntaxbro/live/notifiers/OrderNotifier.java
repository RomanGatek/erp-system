package cz.syntaxbro.live.notifiers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.constants.ObserveActions;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.security.websocket.ProductWebSocketHandler;
import cz.syntaxbro.erpsystem.utils.ConsoleColors;
import cz.syntaxbro.live.EntityNotification;
import cz.syntaxbro.live.observer.EntityNotifier;
import cz.syntaxbro.live.observer.EntityObserver;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderNotifier extends EntityNotifier<Order> implements EntityObserver<Order> {
    private static final Logger logger = LoggerFactory.getLogger(OrderNotifier.class);

    private final ProductWebSocketHandler webSocketHandler;

    @Autowired
    public OrderNotifier(ProductWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void init() {
        ErpSystemApplication.getLogger().info(ConsoleColors.BLUE + "Order Notifier initialized " + ConsoleColors.RESET);
        this.addObserver(this);
    }

    @Override
    public void update(Order order, ObserveActions action) {
        switch (action) {
            case CREATE -> logger.info(ConsoleColors.CYAN + "Order created: {}", order.getId());
            case UPDATE -> logger.info(ConsoleColors.CYAN + "Order updated: {}", order.getId());
            case DELETE -> logger.info(ConsoleColors.CYAN + "Order deleted: {}", order.getId());
            default -> logger.info(ConsoleColors.CYAN + "Unknown action {} for order: {}", action, order.getId());
        }
        EntityNotification<Order> notification = new EntityNotification<>("order", order, action);
        webSocketHandler.broadcast(notification);
    }
}

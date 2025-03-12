package cz.syntaxbro.live.notifiers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.constants.ObserveActions;
import cz.syntaxbro.erpsystem.models.Product;
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
public class ProductNotifier extends EntityNotifier<Product> implements EntityObserver<Product> {

    private static final Logger logger = LoggerFactory.getLogger(ProductNotifier.class);

    private final ProductWebSocketHandler webSocketHandler;

    @Autowired
    public ProductNotifier(ProductWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void init() {
        ErpSystemApplication.getLogger().info(ConsoleColors.BLUE + "Product Notifier initialized " + ConsoleColors.RESET);
        this.addObserver(this);
    }

    @Override
    public void update(Product product, ObserveActions action) {
        switch (action) {
            case CREATE -> logger.info(ConsoleColors.CYAN + "Product created: {}", product.getName());
            case UPDATE -> logger.info(ConsoleColors.CYAN + "Product updated: {}", product.getName());
            case DELETE -> logger.info(ConsoleColors.CYAN + "Product deleted: {}", product.getName());
            default -> logger.info(ConsoleColors.CYAN + "Unknown action {} for product: {}", action, product.getName());
        }
        EntityNotification<Product> notification = new EntityNotification<>("product", product, action);
        webSocketHandler.broadcast(notification);
    }
}

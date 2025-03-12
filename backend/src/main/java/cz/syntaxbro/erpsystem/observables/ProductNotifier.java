package cz.syntaxbro.erpsystem.observables;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.constants.ObserveActions;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.observer.EntityNotifier;
import cz.syntaxbro.erpsystem.observer.EntityObserver;
import cz.syntaxbro.erpsystem.utils.ConsoleColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;


@Component
public class ProductNotifier extends EntityNotifier<Product> implements EntityObserver<Product> {

    private static final Logger logger = LoggerFactory.getLogger(ProductNotifier.class);

    @PostConstruct
    public void init() { this.addObserver(this); }

    @Override
    public void update(Product product, ObserveActions action) {
        switch (action) {
            case CREATE -> logger.info(ConsoleColors.CYAN + "Product created: {}", product.getName());
            case UPDATE -> logger.info(ConsoleColors.CYAN + "Product updated: {}", product.getName());
            case DELETE -> logger.info(ConsoleColors.CYAN + "Product deleted: {}", product.getName());
            default -> logger.info(ConsoleColors.CYAN + "Unknown action {} for product: {}", action, product.getName());
        }
    }
}

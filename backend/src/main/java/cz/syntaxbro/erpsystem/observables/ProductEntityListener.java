package cz.syntaxbro.erpsystem.observables;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.observer.EntityListener;
import cz.syntaxbro.erpsystem.observer.EntityNotifier;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductEntityListener extends EntityListener<Product, EntityNotifier<Product>> {

    static EntityNotifier<Product>  productNotifier;

    @Autowired
    public void setProductNotifier(EntityNotifier<Product> productNotifier) {
        ProductEntityListener.productNotifier = productNotifier;
        super.setNotifier(productNotifier);
    }

    @PostRemove
    @Transactional
    public void afterDelete(Product entity) {
        this.notifier.notifyEntityDeleted(entity);
    }
    @PostPersist
    @Transactional
    public void afterCreate(Product entity) {
       this.notifier.notifyEntityCreated(entity);
    }

    @PostUpdate
    @Transactional
    public void afterUpdate(Product entity) {
        this.notifier.notifyEntityUpdated(entity);
    }
}

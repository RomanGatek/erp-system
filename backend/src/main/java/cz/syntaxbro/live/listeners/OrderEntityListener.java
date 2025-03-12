package cz.syntaxbro.live.listeners;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.live.observer.EntityListener;
import cz.syntaxbro.live.observer.EntityNotifier;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderEntityListener extends EntityListener<Order, EntityNotifier<Order>> {
    static EntityNotifier<Order> orderNotifier;

    @Autowired
    public void setOrderNotifier(EntityNotifier<Order> orderEntityNotifier) {
        OrderEntityListener.orderNotifier = orderEntityNotifier;
        super.setNotifier(orderEntityNotifier);
    }

    @PostRemove
    @Transactional
    public void afterDelete(Order entity) {
        if (this.notifier == null) return;
        this.notifier.notifyEntityDeleted(entity);
    }
    @PostPersist
    @Transactional
    public void afterCreate(Order entity) {
        if (this.notifier == null) return;
        this.notifier.notifyEntityCreated(entity);
    }

    @PostUpdate
    @Transactional
    public void afterUpdate(Order entity) {
        if (this.notifier == null) return;
        this.notifier.notifyEntityUpdated(entity);
    }
}

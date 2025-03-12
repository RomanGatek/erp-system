package cz.syntaxbro.erpsystem.observer;

import cz.syntaxbro.erpsystem.constants.ObserveActions;
import org.springframework.stereotype.Component;

@Component
public class EntityNotifier<T> extends EntityObservable<T> {
    
    public void notifyEntityCreated(T entity) {
        notifyObservers(entity, ObserveActions.CREATE);
    }
    
    public void notifyEntityUpdated(T entity) {
        notifyObservers(entity, ObserveActions.UPDATE);
    }
    
    public void notifyEntityDeleted(T entity) {
        notifyObservers(entity, ObserveActions.UPDATE);
    }
} 
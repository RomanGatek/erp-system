package cz.syntaxbro.live.observer;

import cz.syntaxbro.erpsystem.constants.ObserveActions;

public class EntityNotifier<T> extends EntityObservable<T> {
    
    public void notifyEntityCreated(T entity) {
        notifyObservers(entity, ObserveActions.CREATE);
    }
    
    public void notifyEntityUpdated(T entity) {
        notifyObservers(entity, ObserveActions.UPDATE);
    }
    
    public void notifyEntityDeleted(T entity) {
        notifyObservers(entity, ObserveActions.DELETE);
    }
} 
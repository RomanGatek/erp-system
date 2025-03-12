package cz.syntaxbro.erpsystem.observer;

import cz.syntaxbro.erpsystem.constants.ObserveActions;

import java.util.ArrayList;
import java.util.List;

public class EntityObservable<T> {
    private final List<EntityObserver<T>> observers = new ArrayList<>();

    public void addObserver(EntityObserver<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(EntityObserver<T> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(T entity, ObserveActions action) {
        for (EntityObserver<T> observer : observers) {
            observer.update(entity, action);
        }
    }
}

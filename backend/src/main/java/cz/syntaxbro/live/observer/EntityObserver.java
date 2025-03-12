package cz.syntaxbro.live.observer;

import cz.syntaxbro.erpsystem.constants.ObserveActions;
import jakarta.persistence.Entity;

public interface EntityObserver<T> {
    void update(T entity, ObserveActions action);
}
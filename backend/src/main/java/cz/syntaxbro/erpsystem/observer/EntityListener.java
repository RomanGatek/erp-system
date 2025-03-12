package cz.syntaxbro.erpsystem.observer;

import lombok.Setter;

/**
 * Generic entity listener that can be extended for specific entity types.
 * @param <T> The entity type
 * @param <N> The notifier type
 */
@Setter
public abstract class EntityListener<T, N extends EntityNotifier<T>> {
    protected N notifier;
} 
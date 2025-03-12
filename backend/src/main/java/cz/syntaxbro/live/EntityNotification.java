package cz.syntaxbro.live;

import cz.syntaxbro.erpsystem.constants.ObserveActions;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityNotification<T> {
    private String entityType;
    private T entity;
    private ObserveActions action;
}

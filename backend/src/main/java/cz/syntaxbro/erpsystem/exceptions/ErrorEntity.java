package cz.syntaxbro.erpsystem.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class ErrorEntity {
    private String field;
    @Setter
    private String message;
}
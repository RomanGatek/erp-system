package cz.syntaxbro.erpsystem.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductCategoryRequest {
    private String color;
    private String name;
    private String description;
}

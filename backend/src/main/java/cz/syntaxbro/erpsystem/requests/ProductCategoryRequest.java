package cz.syntaxbro.erpsystem.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Builder
@Data
@Validated
public class ProductCategoryRequest {

    @NotEmpty(message = "color can not be empty")
    private String color;
    @NotEmpty(message = "name cannot be empty")
    private String name;
    private String description;
}

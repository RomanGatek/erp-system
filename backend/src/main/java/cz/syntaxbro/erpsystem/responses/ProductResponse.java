package cz.syntaxbro.erpsystem.responses;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String name;
    private double buyoutPrice;
    private String description;
    private ProductCategory productCategory;
}

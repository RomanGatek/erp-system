package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "product_category")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Name can be null or empty")
    private String name;
    private String description;
    @OneToMany(mappedBy = "productCategory")
    private List<Product> products;
}

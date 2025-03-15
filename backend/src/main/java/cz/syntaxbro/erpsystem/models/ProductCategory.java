package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_categories")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    private String description;
    
    @JsonBackReference
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.REMOVE)
    private List<Product> products;

    private String color;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ProductCategory(Long id, String name, String description, List<Product> products, String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.products = products;
        this.color = color;
    }
}

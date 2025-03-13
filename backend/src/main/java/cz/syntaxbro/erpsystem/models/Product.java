package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Product name is required")
    private String name;

    @Column(nullable = false, name = "buyout_price")
    @Positive(message = "Product buyout price must be greater than zero")
    private double buyoutPrice;

    @Column(nullable = false, name = "purchase_price")
    @Positive(message = "Product purchase price must be greater than zero")
    private double purchasePrice;

    private String description;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category can't be null or empty")
    @JsonManagedReference
    private ProductCategory productCategory;
}
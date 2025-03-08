package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @Min(value=0, message = "Product buyout price must be greater than zero")
    private double buyoutPrice;

    @Column(nullable = false, name = "purchase_price")
    @Min(value=0, message = "Product purchase price must be greater than zero")
    private double purchasePrice;

    private String description;
}
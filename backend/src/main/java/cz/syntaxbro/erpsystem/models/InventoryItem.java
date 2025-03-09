package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Validated
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @NotNull(message = "Product is required")
    private Product product;

    @PositiveOrZero(message = "Item quantity must be zero or positive")
    @Column(name = "stocked_amount")
    private int stockedAmount;

    private LocalDateTime createdAt;
}
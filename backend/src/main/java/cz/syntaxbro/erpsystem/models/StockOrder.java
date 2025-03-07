package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "stock_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product is required")
    private Product product;
    @Positive(message = "Quantity must be greater than zero")
    private int quantity;
    private String comment;
    @Future(message = "Expected delivery date must be in the future")
    private LocalDateTime expectedDeliveryDate;
    @Enumerated(EnumType.STRING)
    private Order.Status status;

}

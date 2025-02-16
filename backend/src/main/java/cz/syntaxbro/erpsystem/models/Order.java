package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotBlank(message = "Product id is required")
    private Product product;

    @Column(nullable = false)
    @NotBlank(message = "Order amount is required")
    private int amount;

    @Column(nullable = false)
    @NotBlank(message = "Order cost is required")
    private double cost;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Status is required")
    private Status status;

    @NotBlank(message = "Time of order is required")
    @Column(name = "order_time")
    private LocalDateTime orderTime;

    public enum Status {
        DONE,
        ORDERED,
        PREORDER
    }
}

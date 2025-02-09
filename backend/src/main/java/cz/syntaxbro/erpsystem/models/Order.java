package cz.syntaxbro.erpsystem.models;

import jakarta.persistence.*;
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
    @JoinColumn(name = "product_id")
    private Product product;

    private int amount;
    private double cost;
    private Status status;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    public enum Status {
        DONE,
        ORDERED,
        PREORDER
    }
}

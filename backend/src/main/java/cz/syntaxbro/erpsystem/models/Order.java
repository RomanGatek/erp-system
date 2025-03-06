package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    private int amount;
    private double cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "order_time")
    private LocalDateTime orderTime;
    
    @Column(name = "comment", length = 500)
    private String comment;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "decision_time")
    private LocalDateTime decisionTime;

    public enum Status {
        PENDING, CONFIRMED, CANCELED
    }
}

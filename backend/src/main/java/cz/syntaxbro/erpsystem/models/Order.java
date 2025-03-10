package cz.syntaxbro.erpsystem.models;

import cz.syntaxbro.erpsystem.requests.OrderRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    private List<OrderItem> orderItems;

    private double cost;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "order_time")
    private LocalDateTime orderTime;
    
    @Column(name = "comment", length = 500)
    private String comment;
    
    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;
    
    @Column(name = "decision_time")
    private LocalDateTime decisionTime;

    public enum Status {
        PENDING, CONFIRMED, CANCELED, IN_TRANSMIT
    }

    public enum OrderType {
        SELL, PURCHASE
    }

    public void map(OrderRequest orderDto) {
        this.cost = orderDto.getCost() == null ? this.cost : orderDto.getCost();
        this.approvedBy = orderDto.getApprovedBy() == null ? this.approvedBy : orderDto.getApprovedBy();
        this.decisionTime = orderDto.getDecisionTime() == null ? this.decisionTime : orderDto.getDecisionTime();
        this.status = orderDto.getStatus() == null ? this.status : orderDto.getStatus();
        this.orderItems = (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty())
                ? this.orderItems
                : orderDto.getOrderItems();
        this.comment = orderDto.getComment() == null ? this.comment : orderDto.getComment();
    }
}

package cz.syntaxbro.erpsystem.models;

import cz.syntaxbro.erpsystem.requests.OrderRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;
    
    @Column(name = "decision_time")
    private LocalDateTime decisionTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

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

    public double getTotal(Order.OrderType orderType) {
        double total = 0;
        for (OrderItem item : orderItems) {
            Product product = item.getInventoryItem().getProduct();
            total += item.getQuantity() * (orderType.equals(OrderType.SELL) ? product.getBuyoutPrice() : product.getPurchasePrice());
        }
        return total;
    }

    public BigDecimal getTotal() {
        double total = 0;
        for (OrderItem item : orderItems) {
            Product product = item.getInventoryItem().getProduct();
            total += item.getQuantity() * (item.getOrder().getOrderType().equals(OrderType.SELL)
                    ? product.getBuyoutPrice()
                    : product.getPurchasePrice()
            );
        }
        return BigDecimal.valueOf(total);
    }

    @SuppressWarnings("unused")
    public void recalculateTotal() {
        this.cost = this.orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * (
                        this.orderType == OrderType.SELL
                                ? item.getInventoryItem().getProduct().getBuyoutPrice()
                                : item.getInventoryItem().getProduct().getPurchasePrice()))
                .sum();
    }

    public Order(OrderType orderType, Long id, List<OrderItem> orderItems, double cost, Status status, LocalDateTime orderTime, String comment, User approvedBy, LocalDateTime decisionTime) {
        this.orderType = orderType;
        this.id = id;
        this.orderItems = orderItems;
        this.cost = cost;
        this.status = status;
        this.orderTime = orderTime;
        this.comment = comment;
        this.approvedBy = approvedBy;
        this.decisionTime = decisionTime;
    }
}

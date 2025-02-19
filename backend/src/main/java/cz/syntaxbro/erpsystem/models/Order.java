package cz.syntaxbro.erpsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @JsonDeserialize(using = NumberDeserializers.IntegerDeserializer.class)
    private int amount;

    @Column(nullable = false)
    @JsonDeserialize(using = NumberDeserializers.DoubleDeserializer.class)
    private Double cost;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    public enum Status {
        DONE,
        ORDERED,
        PREORDER;

        public static Status fromString(String statusStr) {
            try {
                return Status.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + statusStr
                        + ". Valid statuses are: " + Arrays.toString(Status.values()));
            }
        }

        public String getStatusMessage() {
            return switch (this) {
                case DONE -> "Order is completed";
                case ORDERED -> "Order is active";
                case PREORDER -> "Order is no active";
                default -> "Unknown status";
            };
        }
    }

    public String getOrderStatus() {
        return status.getStatusMessage();
    }

    public static Order fromOrderDto(OrderDto request, ProductService productService) {
        Order order = new Order();

        order.setProduct(validateAndGetProduct(Math.toIntExact(request.getProductId()), productService));
        order.setAmount(validateAmount(request.getAmount()));
        order.setCost(validateCost(request.getCost()));
        order.setOrderTime((validateAndParseOrderTime(((request.getOrderTime())))));
        order.setStatus(Status.fromString(request.getStatus()));

        return order;
    }

    private static Product validateAndGetProduct(Integer productId, ProductService productService) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (!(productId instanceof Integer)) {
            throw new IllegalArgumentException("Product ID must be an Integer");
        }
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        Product product = productService.getProductById(productId.longValue());
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist!");
        }
        return product;
    }

    private static int validateAmount(Integer amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(0, RoundingMode.HALF_UP);

        if (bd.doubleValue() != amount) {
            throw new IllegalArgumentException("Amount must be an integer without any decimal places");
        }
        if (amount != (int) amount) {
            throw new IllegalArgumentException("Amount must be an integer");
        }
        return amount;
    }

    private static double validateCost(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("Cost is required");
        }
        BigDecimal bd = new BigDecimal(cost);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        if (bd.doubleValue() != cost) {
            throw new IllegalArgumentException("Cost must be a double and have exactly 2 decimal places");
        }

        if (cost <= 0) {
            throw new IllegalArgumentException("Cost must be positive");
        }
        return cost;
    }

    private static LocalDateTime validateAndParseOrderTime(LocalDateTime orderTime) {
        if (orderTime == null) {
            throw new IllegalArgumentException("Order time is required");
        }

        try {

            String isoDateTime = orderTime.toString();
            LocalDateTime.parse(isoDateTime);

            String pattern = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d";

            if (!isoDateTime.matches(pattern)) {
                throw new IllegalArgumentException("Invalid date format. Use: yyyy-MM-ddTHH:mm:ss");
            }

            if (orderTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Order time cannot be in the future");
            }

            return orderTime;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss");
        }
    }
}
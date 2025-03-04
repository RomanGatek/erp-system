package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.services.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@EnableMethodSecurity()
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(
            @PathVariable(name = "id")
            @NotNull(message = "ID can't be null")
            Long id) {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/cost-between")
    public ResponseEntity<List<Order>> getOrdersByCost(
            @RequestParam(value = "start", defaultValue = "0")
            @NotNull
            @Min(value = 0, message = "Cost must be grater or equal with 0")
            double start,
            @RequestParam(value = "end", defaultValue = "0")
            @NotNull
            @Min(value = 0, message = "Cost must be grater or equal with 0")
            double end) {
            List<Order> orders = orderService.getOrdersByCostBetween(start, end);
            return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-between")
    public ResponseEntity<List<Order>> getOrdersByDateBetween(
            @RequestParam(name = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam(name = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now().toLocalDate().atStartOfDay();
        }
        if (end == null) {
            end = LocalDateTime.now().with(LocalTime.MAX);
        }
        List<Order> orders = orderService.getOrdersByDateBetween(start, end);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Order>> getOrdersByProduct(
            @RequestParam(value = "productId", defaultValue = "0")
            @NotNull(message = "Product id cant be null")
            Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(
            @RequestParam @Min(value = 1, message = "Must be a number") Long itemId,
            @RequestParam @Min(value = 1, message = "Must be positive number") int quantity) {
        Order createdOrder = orderService.createdOrder(itemId, quantity);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable @Min(value = 1, message = "Must be a number") Long orderId) {
        orderService.cancelOrder(orderId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(
            @PathVariable(name = "id")
            @NotNull
            Long id,
            @RequestBody
            @Valid
            OrderRequest orderDto) {
        orderService.updateOrder(id, orderDto);
        return ResponseEntity.ok("Order with id " + id + " is updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable(name = "id") Long id) {
            orderService.deleteOrder(id);
        return ResponseEntity.ok(String.format("Order with id %s is deleted", id));
    }

    @DeleteMapping("/delete-orders-with-product/{id}")
    public ResponseEntity<String> deleteOrdersWithProductId(
            @PathVariable(name = "id")
            @NotNull
            Long id) {
            orderService.deleteOrderByProductId(id);
            return ResponseEntity.ok("Orders with product id " + id + " are deleted");
    }
}

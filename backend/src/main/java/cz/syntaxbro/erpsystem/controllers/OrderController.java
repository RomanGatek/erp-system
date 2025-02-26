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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrder(
            @PathVariable(name = "id")
            @NotNull(message = "ID can't be null")
            Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/cost-between")
    public ResponseEntity<List<Object>> getOrdersByCost(
            @RequestParam(value = "start", defaultValue = "0")
            @NotNull
            @Min(value = 0, message = "Cost must be grater or equal with 0")
            double start,
            @RequestParam(value = "end", defaultValue = "0")
            @NotNull
            @Min(value = 0, message = "Cost must be grater or equal with 0")
            double end) {
        try {
            List<Order> orders = orderService.getOrdersByCostBetween(start, end);
            return ResponseEntity.ok(List.of(orders));
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @GetMapping("/date-between")
    public ResponseEntity<List<Object>> getOrdersByDateBetween(
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
        try {
            List<Order> orders = orderService.getOrdersByDateBetween(start, end);
            return ResponseEntity.ok(List.of(orders));
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Object>> getOrdersByProduct(
            @RequestParam(value = "productId", defaultValue = "0")
            @NotNull(message = "Product id cant be null")
            Long productId) {
        try{
            return ResponseEntity.ok(List.of(orderService.getOrdersByProduct(productId)));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createOrder(
            @RequestBody
            @Valid
            OrderRequest orderDto) {
        try{
            Order createdOrder = orderService.createdOrder(orderDto);
            return ResponseEntity.ok("Order is created \n" + createdOrder);
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateOrder(
            @PathVariable(name = "id")
            @NotNull
            Long id,
            @RequestBody
            @Valid
            OrderRequest orderDto) {
        try{
            orderService.updateOrder(id, orderDto);
            return ResponseEntity.ok("Order is updated \n" + orderDto);
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable(name = "id") Long id) {
        try{
            orderService.deleteOrder(id);
            return ResponseEntity.ok(String.format("Order with id %s is deleted", id));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-orders-with-product/{id}")
    public ResponseEntity<List<Object>> deleteOrdersWithProductId(
            @PathVariable(name = "id")
            @NotNull
            Long id) {
        try {
            orderService.deleteOrderByProductId(id);
            return ResponseEntity.ok(List.of(String.format("Orders with product %s deleted", id)));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }
}

package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.validates.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {


    private final OrderService orderService;


    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable(name = "id") Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getOrders();
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @GetMapping("/cost-between")
    public ResponseEntity<List<Order>> getOrdersByCost(
            @RequestParam(name = "start") Long start,
            @RequestParam(name = "end") Long end) {
        List<Order> orders = orderService.getOrdersByCostBetween(start, end);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-between")
    public ResponseEntity<List<Order>> getOrdersByDateBetween(
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        List<Order> orders = orderService.getOrdersByDateBetween(start, end);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Order>> getOrdersByProduct(@RequestParam(name = "productId") Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest requestBody) {
            Order createdOrder = orderService.createOrder(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable(name = "id") Long id, @Valid @RequestBody OrderRequest requestBody) {
        Order updatedOrder = orderService.updateOrder(id, requestBody);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable(name = "id") Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body("Invalid order ID");
        }

        try{
            if (!orderService.existOrderById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("Order with ID %d does not exist", id));
            }

            orderService.deleteOrder(id);
            return ResponseEntity.ok(String.format("Order %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting order: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-orders-with-product/{id}")
    public ResponseEntity<String> deleteOrdersWithProductId(@PathVariable(name = "id") Long id) {
        if (id <= 0){
            return ResponseEntity.badRequest().body("Invalid product ID");
        }

        try{
            if (!orderService.existOrderByProductId(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("Orders with product ID %d does not exist", id));
            }
            orderService.deleteOrderByProductId(id);
            return ResponseEntity.ok(String.format("Orders with product ID %d deleted successfully", id));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting orders: " + e.getMessage());
        }
    }
}

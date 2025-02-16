package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.services.OrderService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //Metoda na spracovanie chyb validacie
    private ResponseEntity<Map<String,String>>
    handleValidationErrors(BindingResult result) {
        Map<String,String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(order);
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
            @RequestParam Long start,
            @RequestParam Long end) {
        List<Order> orders = orderService.getOrdersByCostBetween(start, end);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-between")
    public ResponseEntity<List<Order>> getOrdersByDateBetween(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<Order> orders = orderService.getOrdersByDateBetween(start, end);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Order>> getOrdersByProduct(@RequestParam Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody Order order,
                                         BindingResult result) {
        if (result.hasErrors()){
            return handleValidationErrors(result);
        }
        //Dalsie validacie
        if (order.getAmount() <= 0){
            return ResponseEntity.badRequest().body("Order amount must be positive");
        }
        if (order.getCost() <= 0){
            return ResponseEntity.badRequest().body("Order cost must be positive");
        }
        if (order.getProduct() == null || order.getProduct().getId() == null){
            return ResponseEntity.badRequest().body("Valid product must be specified");
        }

        try {
            orderService.createOrder(order);
            return ResponseEntity.ok(String.format("Order created successfully with ID: %d", order.getId()));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating order:" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order, BindingResult result) {
        if (result.hasErrors()){
            return handleValidationErrors(result);
        }

        if (id <= 0) {
            return ResponseEntity.badRequest().body("Invalid order ID");
        }

        //Dodatocne validacie
        if (order.getAmount() <= 0){
            return ResponseEntity.badRequest().body("Order amount must be positive");
        }
        if (order.getCost() <= 0){
            return ResponseEntity.badRequest().body("Order cost must be positive");
        }
        if (order.getProduct() == null || order.getProduct().getId() == null){
            return ResponseEntity.badRequest().body("Valid product must be specified");
        }

        try{
            orderService.updateOrder(id, order);
            return ResponseEntity.ok(String.format("Order %d updated successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating order: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body("Invalid order ID");
        }

        try{
            orderService.deleteOrder(id);
            return ResponseEntity.ok(String.format("Order %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting order: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-orders-with-product/{id}")
    public ResponseEntity<String> deleteOrdersWithProductId(@PathVariable Long id) {
        if (id <= 0){
            return ResponseEntity.badRequest().body("Invalid product ID");
        }

        try{
            orderService.deleteOrderByProductId(id);
            return ResponseEntity.ok(String.format("Orders with product ID %d deleted successfully", id));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting orders: " + e.getMessage());
        }
    }
}

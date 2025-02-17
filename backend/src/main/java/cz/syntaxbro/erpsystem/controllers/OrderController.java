package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.dtos.OrderDto;
import cz.syntaxbro.erpsystem.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public ResponseEntity<Order> getOrder(@PathVariable(name = "id") Long id) {
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
    public ResponseEntity<String> getOrdersByCost(
            @RequestParam(value = "start", defaultValue = "0") double start,
            @RequestParam(value = "end", defaultValue = "0") double end) {
        try {
            List<Order> orders = orderService.getOrdersByCostBetween(start, end);
            return ResponseEntity.ok(orders.toString());
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/date-between")
    public ResponseEntity<String> getOrdersByDateBetween(
            @RequestParam(name = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

            @RequestParam(name = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        try {

            List<Order> orders = orderService.getOrdersByDateBetween(start, end);
            return ResponseEntity.ok(orders.toString());
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Order>> getOrdersByProduct(@RequestParam("productId") Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto) {
        try{
            Order createdOrder = orderService.createdOrder(orderDto);
            return ResponseEntity.ok(String.format("Order %s created", createdOrder));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable(name = "id") Long id, @RequestBody Order order) {
        try{
            orderService.updateOrder(id, order);
            return ResponseEntity.ok(String.format("Order %s updated", order));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable(name = "id") Long id) {
        try{
            orderService.deleteOrder(id);
            return ResponseEntity.ok(String.format("Order %s deleted", id));
        }catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-orders-with-product/{id}")
    public ResponseEntity<String> deleteOrdersWithProductId(@PathVariable(name = "id") Long id) {
        orderService.deleteOrderByProductId(id);
        return ResponseEntity.ok(String.format("Order with product %s deleted", id));
    }
}

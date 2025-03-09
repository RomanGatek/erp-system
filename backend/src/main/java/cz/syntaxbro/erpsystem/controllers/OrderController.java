package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.requests.OrderCreateRequest;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.responses.OrderResponse;
import cz.syntaxbro.erpsystem.services.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/by-product")
    public ResponseEntity<List<Order>> getOrdersByProduct(
            @RequestParam(value = "productId", defaultValue = "0")
            @NotNull(message = "Product id cant be null")
            Long productId) {
        List<Order> orders = orderService.getOrdersByProduct(productId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderCreateRequest> createOrder(@RequestBody OrderCreateRequest orderRequest) {
        System.out.println(orderRequest);

//        Order createdOrder = orderService.createdOrder(orderRequest);
        return ResponseEntity.ok(orderRequest);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable @Min(value = 1, message = "Must be a number") Long orderId,
            @RequestBody(required = false) String comment) {
        Order canceledOrder = orderService.cancelOrder(orderId, comment);
        return ResponseEntity.ok(canceledOrder);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(
            @PathVariable(name = "id") @NotNull Long id,
            @RequestBody(required = false) String comment) {

        Order confirmedOrder = orderService.confirmOrder(id, comment);
        return ResponseEntity.ok(confirmedOrder);
    }

    @PutMapping("/{id}/workflow-comment")
    public ResponseEntity<Order> addWorkflowComment(
            @PathVariable(name = "id") @NotNull Long id,
            @RequestBody @NotNull @Size(max = 500) String comment) {

        Order updatedOrder = orderService.addWorkflowComment(id, comment);
        return ResponseEntity.ok(updatedOrder);
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


    @PutMapping("/{orderId}/change-status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable ("orderId") long orderId, @RequestParam Order.Status status) {
        orderService.updateOrderStatus(orderId, status);
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}

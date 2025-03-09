package cz.syntaxbro.erpsystem.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    /**
     * Test: Ensures that an order is correctly created with all expected values.
     * Expected Result:
     * - The order should be assigned the correct ID.
     * - The associated product should be correctly set.
     * - The amount, cost, and status should match the provided values.
     */
    @Test
    public void testOrderCreation() {
        // Arrange: Create a new product instance (assuming Product class exists)
        OrderItem orderItem =  new OrderItem();

        // Act: Create a new order with specified values
       LocalDateTime now = LocalDateTime.now();
       Order order = Order.builder()
                .id(1L)
                .orderTime(now)
                .orderItems(List.of(orderItem))
                .orderType(Order.OrderType.SELL)
                .decisionTime(now.plusDays(1))
                .cost(100)
                .comment("comment")
                .status(Order.Status.PENDING)
                .build();

        // Assert: Verify that all order attributes were set correctly
        assertEquals(1L, order.getId(), "Order ID should be 1");
        assertEquals(orderItem, order.getOrderItems().getFirst(), "Order product should match the provided product");
        assertEquals(100.0, order.getCost(), "Order cost should be 100.0");
        assertEquals(Order.Status.PENDING, order.getStatus(), "Order status should be ORDERED");
    }
}

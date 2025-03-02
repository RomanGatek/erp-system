package cz.syntaxbro.erpsystem.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        Product product = new Product();

        // Act: Create a new order with specified values
        Order order = new Order(1L, product, 5, 100.0, Order.Status.ORDERED, LocalDateTime.now());

        // Assert: Verify that all order attributes were set correctly
        assertEquals(1L, order.getId(), "Order ID should be 1");
        assertEquals(product, order.getProduct(), "Order product should match the provided product");
        assertEquals(5, order.getAmount(), "Order amount should be 5");
        assertEquals(100.0, order.getCost(), "Order cost should be 100.0");
        assertEquals(Order.Status.ORDERED, order.getStatus(), "Order status should be ORDERED");
    }
}

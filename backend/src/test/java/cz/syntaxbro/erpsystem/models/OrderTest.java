package cz.syntaxbro.erpsystem.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        Product product = new Product(); // Předpokládáme, že máte třídu Product
        Order order = new Order(1L, product, 5, 100.0, Order.Status.SHIPPED, LocalDateTime.now());

        assertEquals(1L, order.getId());
        assertEquals(product, order.getProduct());
        assertEquals(5, order.getAmount());
        assertEquals(100.0, order.getCost());
        assertEquals(Order.Status.SHIPPED, order.getStatus());
    }
} 
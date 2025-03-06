package cz.syntaxbro.erpsystem.events;

import cz.syntaxbro.erpsystem.models.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderStatusChangedEventTest {

    @Test
    void testOrderStatusChangedEvent() {
        // Arrange
        Long expectedOrderId = 1L;
        Order.Status expectedStatus = Order.Status.SHIPPED;

        // Act
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(expectedOrderId, expectedStatus);

        // Assert
        assertEquals(expectedOrderId, event.getOrderId(), "Order ID should match");
        assertEquals(expectedStatus, event.getStatus(), "Order status should match");
    }
}

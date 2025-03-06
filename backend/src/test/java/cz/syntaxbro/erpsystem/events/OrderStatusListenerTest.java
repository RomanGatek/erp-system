package cz.syntaxbro.erpsystem.events;

import cz.syntaxbro.erpsystem.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class OrderStatusListenerTest {

    @Mock
    private Logger logger;

    private OrderStatusListener orderStatusListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderStatusListener = new OrderStatusListener(logger);
    }

    @Test
    void testHandleOrderStatusChanged() {
        // Arrange
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(1L, Order.Status.SHIPPED);

        // Act
        orderStatusListener.handleOrderStatusChanged(event);

        // Assert
        verify(logger, times(1)).info("Order {} changed status to {}", 1L, Order.Status.SHIPPED);
    }
}

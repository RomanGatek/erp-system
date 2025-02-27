package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.impl.InventoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryItem item1;
    private InventoryItem item2;
    private InventoryItem item3;

    @BeforeEach
    void setUp() {
        item1 = new InventoryItem(1L, "Test Item", 10);
        item2 = new InventoryItem(2L, "Test Item", 5);
        item3 = new InventoryItem(3L, "Test Item", 5);
    }

    @Test
    void testAddItem() {
        when(inventoryRepository.save(item1)).thenReturn(item1);

        InventoryItem result = inventoryService.addItem(item1);

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals("Test Item", result.getName());
    }

    @Test
    void testUpdateQuantity() {
        when(inventoryRepository.updateQuantity(1L, 20)).thenReturn(1);

        assertDoesNotThrow(() -> inventoryService.updateQuantity(1L, 20));

        verify(inventoryRepository, times(1)).updateQuantity(1L, 20);
    }

    @Test
    void testUpdateInvalidQuantity() {
// Arrange: Mock the repository to return 0 (no rows updated)
        when(inventoryRepository.updateQuantity(99L, 20)).thenReturn(0);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                inventoryService.updateQuantity(99L, 20)
        );

        assertEquals("Item with id 99 not found", exception.getMessage());

        verify(inventoryRepository, times(1)).updateQuantity(99L, 20);
    }
}
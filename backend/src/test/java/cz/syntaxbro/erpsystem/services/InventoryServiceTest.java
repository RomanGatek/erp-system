package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.impl.InventoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryItem item1;

    private final Product product = new Product(1L, "Test Product", 50.0, "Sample product description");

    @BeforeEach
    void setUp() {
        item1 = new InventoryItem(1L, product, 10);
    }

    @Test
    void testAddItem() {
        when(inventoryRepository.save(item1)).thenReturn(item1);

        InventoryItem result = inventoryService.addItem(item1);

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals("Test Product", result.getProduct().getName());
    }

    @Test
    void testUpdateQuantity() {
        Long itemId = 1L;
        int newQuantity = 20;

        when(inventoryRepository.updateQuantity(itemId, newQuantity)).thenReturn(1);

        assertDoesNotThrow(() -> inventoryService.updateQuantity(itemId, newQuantity));

        verify(inventoryRepository, times(1)).updateQuantity(itemId, newQuantity);
    }

    @Test
    void testUpdateInvalidQuantity() {
        Long itemId = 1L;
        int newQuantity = 20;

        when(inventoryRepository.updateQuantity(itemId, newQuantity)).thenReturn(0);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                inventoryService.updateQuantity(itemId, newQuantity)
        );

        assertEquals("Item with id 1 not found", exception.getMessage());

        verify(inventoryRepository, times(1)).updateQuantity(itemId, newQuantity);
    }


    @Test
    void receiveStock_successfully() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(item1));
        inventoryService.receiveStock(1L, 100);
        assertEquals(110, item1.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void releaseStock_successfully() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(item1));
        inventoryService.receiveStock(1L, 400);
        inventoryService.releaseStock(1L, 1);
        assertEquals(409, item1.getQuantity());
        verify(inventoryRepository, times(2)).findById(1L);
    }

    @Test void releaseStock_config_noEnoughQuantity() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(item1));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> inventoryService.releaseStock(1L, 300)
        );
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatusCode());
        assertEquals("not enough quantity of product", exception.getReason());
    }
}
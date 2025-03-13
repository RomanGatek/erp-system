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

    private Product product;

    @BeforeEach
    void setUp() {
        this.product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Sample product description")
                .buyoutPrice(10)
                .purchasePrice(20)
                .build();


        this.item1 = InventoryItem.builder()
                .id(1L)
                .product(this.product)
                .stockedAmount(10)
                .build();
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
}
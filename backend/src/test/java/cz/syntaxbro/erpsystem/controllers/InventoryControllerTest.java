package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryItem sampleItem;

    @BeforeEach
    void setUp() {
        Product sampleProduct = Product.builder()
                .id(1L)
                .purchasePrice(10)
                .buyoutPrice(10)
                .description("test description")
                .name("testName")
                .build();

        this.sampleItem = InventoryItem.builder()
                .id(1L)
                .stockedAmount(10)
                .product(sampleProduct)
                .build();
    }

    /**
     * Test: Releasing stock from an inventory item
     * Expected result: 200 OK + updated stock details
     */
    @Test
    void testGetItem() {
        when(inventoryService.getItem(anyLong())).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.getItem(1L);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals(1L, response.getBody().getId());
        assertEquals("testName", response.getBody().getProduct().getName(), "Verifies the product name is correct");
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testAddItem() {
        when(inventoryService.addItem(any(InventoryItem.class))).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.addItem(sampleItem);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals("testName", response.getBody().getProduct().getName(), "Verifies the product name is correct");
    }

    /**
     * Test: Updating an inventory item
     * Expected result: 200 OK + updated item details
     */
    @Test
    void testUpdateItem() {
        when(inventoryService.updateItem(anyLong(), any(InventoryItem.class))).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.updateItem(1L, sampleItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Ensures the response body is not null");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals("testName", response.getBody().getProduct().getName(), "Verifies the product name is correct");
    }

    /**
     * Test: Fetching all inventory items
     * Expected result: 200 OK + list of items
     */
    @Test
    void testGetAllItems() {
        List<InventoryItem> items = Arrays.asList(sampleItem);
        when(inventoryService.getAll()).thenReturn(items);

        ResponseEntity<List<InventoryItem>> response = inventoryController.getAllItems();
        
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Test: Removing an inventory item
     * Expected result: 200 OK status
     */
    @Test
    void testRemoveItem() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(sampleItem));
        doNothing().when(inventoryRepository).delete(any(InventoryItem.class));

        ResponseEntity<?> response = inventoryController.removeItem(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
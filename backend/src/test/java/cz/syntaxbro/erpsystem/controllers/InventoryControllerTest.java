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
        sampleItem = new InventoryItem();
        sampleItem.setId(1L);

        Product testProduct = new Product();
        testProduct.setName("Test Product");

        sampleItem.setProduct(testProduct);
        sampleItem.setQuantity(10);
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
        assertNotNull(response.getBody(), "Ensures the response body is not null");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals(1L, response.getBody().getId());
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals("Test Product", response.getBody().getProduct().getName(), "Verifies the product name is correct");
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
        assertNotNull(response.getBody(), "Ensures the response body is not null");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals("Test Product", response.getBody().getProduct().getName(), "Verifies the product name is correct");
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testUpdateItem() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(sampleItem));
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.updateItem(1L, sampleItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Ensures the response body is not null");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertNotNull(response.getBody().getProduct(), "Ensures the product is not null before accessing its properties");
        assertEquals("Test Product", response.getBody().getProduct().getName(), "Verifies the product name is correct");
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testGetAllItems() {
        when(inventoryRepository.findAll()).thenReturn(List.of(sampleItem));
        ResponseEntity<List<InventoryItem>> response = inventoryController.getAllItems();
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testRemoveItem() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(sampleItem));
        doNothing().when(inventoryRepository).delete(any(InventoryItem.class));

        ResponseEntity<?> response = inventoryController.removeItem(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testReceiveItem() {
        doNothing().when(inventoryService).receiveStock(anyLong(), anyInt());
        when(inventoryService.getItem(anyLong())).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.receiveItem(1L, 5);
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    /**
     * Test: Fetching a single inventory item
     * Expected result: 200 OK + item details
     */
    @Test
    void testReleaseItem() {
        doNothing().when(inventoryService).releaseStock(anyLong(), anyInt());
        when(inventoryService.getItem(anyLong())).thenReturn(sampleItem);

        ResponseEntity<InventoryItem> response = inventoryController.releaseItem(1L, 3);
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }
}
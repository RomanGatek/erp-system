package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.repositories.InventoryRepository;
import cz.syntaxbro.erpsystem.services.impl.InventoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Spy
    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryItem item1;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    void testGetAll_Successfully(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryRepository.findAll()).thenReturn(List.of(this.item1));
        //Act
        List<InventoryItem> result = inventoryService.getAll();
        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryService, times(1)).getAll();
    }

    @Test
    void findItemByProductForOrder_Successfully(){
        //Arrest
        when(inventoryRepository.findByProduct(this.product)).thenReturn(Optional.of(this.item1));
        //Act
        Optional<InventoryItem> result = inventoryService.findItemByProductForOrder(this.product);
        //Assert
        assertNotNull(result);
        assertEquals(product.getName(), result.get().getProduct().getName());
        verify(inventoryService, times(1)).findItemByProductForOrder(this.product);
    }

    @Test
    void testGetItem_successfully(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        //Act
        InventoryItem result = inventoryService.getItem(id);
        //Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(inventoryService, times(1)).getItem(id);
    }

    @Test
    void testGetItem_NoFoundInventoryItem(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                inventoryService.getItem(id));
        //Assert

        assertEquals(String.format("Item with id %d not found", id), exceptionResult.getMessage());
        verify(inventoryService, times(1)).getItem(id);
    }

    @Test
    void testFindItemByProduct_Successfully(){
        //Arrest
        when(inventoryRepository.findByProduct(this.product)).thenReturn(Optional.of(this.item1));
        //Act
        InventoryItem result = inventoryService.findItemByProduct(this.product);
        //Assert
        assertNotNull(result);
        assertEquals(this.product.getName(), result.getProduct().getName());
        verify(inventoryService, times(1)).findItemByProduct(this.product);
    }

    @Test
    void testFindItemByProduct_NotFoundProduct(){
        //Arrest

        when(inventoryRepository.findByProduct(this.product)).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                inventoryService.findItemByProduct(this.product));
        //Assert
        assertEquals(String.format("Inventory item for product '%s' not found", product.getName()), exceptionResult.getMessage());
        verify(inventoryService, times(1)).findItemByProduct(this.product);
    }

    @Test
    void testIsStockAvailable_forTrue(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryService.getItem(id)).thenReturn(item1);
        //Act
        boolean result = inventoryService.isStockAvailable(id, this.item1.getStockedAmount() - 1);
        //Assert
        assertTrue(result);
        verify(inventoryService, times(1)).isStockAvailable(id, this.item1.getStockedAmount() - 1);
    }

    @Test
    void testIsStockAvailable_forFalse(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryService.getItem(id)).thenReturn(item1);
        //Act
        boolean result = inventoryService.isStockAvailable(id, this.item1.getStockedAmount() + 1);
        //Assert
        assertFalse(result);
        verify(inventoryService, times(1)).isStockAvailable(id, this.item1.getStockedAmount() + 1);
    }

    @Test
    void testReceiveStock(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryRepository.save(item1)).thenReturn(item1);
        //Act
        inventoryService.receiveStock(id, 10);
        //Assert
        assertEquals(20, item1.getStockedAmount());
        verify(inventoryService, times(1)).receiveStock(id, 10);
    }

    @Test
    void testReleaseStock_successfully(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryRepository.save(item1)).thenReturn(item1);
        //Act
        inventoryService.releaseStock(id, 10);
        //Assert
        assertEquals(0, item1.getStockedAmount());
        verify(inventoryService, times(1)).releaseStock(id, 10);
    }

    @Test
    void testReleaseStock_WithQuantityGreaterThanAmount(){
        //Arrest
        int quantityGreaterThanAmount = this.item1.getStockedAmount() + 1;
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(this.item1));
        //Act
        ResponseStatusException exceptionResult = assertThrows(ResponseStatusException.class, () ->
            inventoryService.releaseStock(id, quantityGreaterThanAmount));
        //Assert
        assertEquals("not enough quantity of product", exceptionResult.getReason());
        assertEquals("406 NOT_ACCEPTABLE \"not enough quantity of product\"", exceptionResult.getMessage());
        verify(inventoryService, times(1)).releaseStock(id, quantityGreaterThanAmount);
    }



    @Test
    void testUpdateItem_Successfully(){
        long id = 1L;
        Product productForUpdate = Product.builder()
                .buyoutPrice(10)
                .purchasePrice(8)
                .name("update title")
                .description("update description")
                .productCategory(new ProductCategory())
                .build();

        InventoryItem inventoryItemDto = InventoryItem.builder()
                .product(productForUpdate)
                .stockedAmount(20)
                .build();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryRepository.save(item1)).thenReturn(item1);
        //Act
        InventoryItem result = inventoryService.updateItem(id, inventoryItemDto);
        //Assert
        assertNotNull(result);
        assertEquals(20, result.getStockedAmount());
        assertEquals(productForUpdate, result.getProduct());
        verify(inventoryService, times(1)).updateItem(id, inventoryItemDto);
    }

    @Test
    void testUpdateItem_NotFoundInventoryItem(){
        long id = 1L;
        Product productForUpdate = Product.builder()
                .buyoutPrice(10)
                .purchasePrice(8)
                .name("update title")
                .description("update description")
                .productCategory(new ProductCategory())
                .build();

        InventoryItem inventoryItemDto = InventoryItem.builder()
                .product(productForUpdate)
                .stockedAmount(20)
                .build();
        //Arrest
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                inventoryService.updateItem(id, inventoryItemDto));
        assertEquals("Inventory Item not found with id: " + id, exceptionResult.getMessage());
        verify(inventoryService, times(1)).updateItem(id, inventoryItemDto);
    }

    @Test
    void TestReserveStock_WhenQuantitySmallerThan0(){
        //Arrest
        long id = 1L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        //Act
        int greaterQuantityThanAmount = item1.getStockedAmount()+1;
        IllegalArgumentException exceptionResult = assertThrows(IllegalArgumentException.class, () ->
                inventoryService.reserveStock(id, greaterQuantityThanAmount));
        //Assert
        assertEquals("We do not have enough quantity of this item.", exceptionResult.getMessage());
        verify(inventoryService, times(1)).reserveStock(id, greaterQuantityThanAmount);
    }

    @Test
    void TestReserveStock_WhenQuantityGreaterOrEqual0WithRowEquals0(){
        //Arrest
        long id = 1L;
        int lesserQuantityThanAmount = item1.getStockedAmount()-1;
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(item1));
        when(inventoryRepository.updateQuantity(id, lesserQuantityThanAmount)).thenReturn(0);
        //Act
        EntityNotFoundException exeptionResult = assertThrows(EntityNotFoundException.class, () ->
            inventoryService.reserveStock(id, lesserQuantityThanAmount));
        //Assert
        assertEquals(String.format("Item with id %d not found", id), exeptionResult.getMessage());
        verify(inventoryService, times(1)).reserveStock(id, lesserQuantityThanAmount);
    }


}
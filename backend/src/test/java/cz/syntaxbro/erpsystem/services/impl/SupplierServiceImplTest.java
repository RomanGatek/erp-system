package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.models.Supplier;
import cz.syntaxbro.erpsystem.repositories.ProductCategoryRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.repositories.SupplierRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Spy
    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private Product product;
    private ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //create supplier
        this.supplier = Supplier.builder()
                .name("supplier")
                .email("test@test.com")
                .address("address")
                .build();

        //Create category
        this.productCategory = ProductCategory.builder()
                .name("test category")
                .description("test")
                .color("Blue")
                .build();

        //create product
        this.product = Product.builder()
                .name("test")
                .productCategory(this.productCategory)
                .purchasePrice(100)
                .buyoutPrice(80)
                .supplier(this.supplier)
                .build();
    }

    @Test
    void getSupplierById_Successfully() {
        //Arrest
        long supplierId = 1L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(this.supplier));
        //Act
        Supplier result = supplierService.getSupplierById(supplierId);
        //Assert
        assertNotNull(result);
        assertEquals(result.getName(), this.supplier.getName());
        verify(supplierService, times(1)).getSupplierById(supplierId);
    }

    @Test
    void getSupplierById_NoExistingSupplierId() {
        //Arrest
        long supplierId = 1L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                supplierService.getSupplierById(supplierId));
        //Assert
        assertEquals("Supplier not found", exceptionResult.getMessage());
        verify(supplierService, times(1)).getSupplierById(supplierId);
    }

    @Test
    void getSupplierByName_Successfully() {
        //Arrest
        when(supplierRepository.findByCompanyName(any(String.class))).thenReturn(Optional.of(this.supplier));
        //Act
        Supplier result = supplierService.getSupplierByName(this.supplier.getName());
        //Assert
        assertNotNull(result);
        assertEquals(result.getName(), this.supplier.getName());
        verify(supplierService, times(1)).getSupplierByName(this.supplier.getName());
    }

    @Test
    void getSupplierByName_NoExistingSupplierName() {
        //Arrest
        when(supplierRepository.findByCompanyName(any(String.class))).thenReturn(Optional.empty());
        //Act
        EntityNotFoundException exceptionResult = assertThrows(EntityNotFoundException.class, () ->
                supplierService.getSupplierByName(this.supplier.getName()));
        //Assert
        assertEquals("Supplier not found", exceptionResult.getMessage());
        verify(supplierService, times(1)).getSupplierByName(this.supplier.getName());
    }

    @Test
    void getSuppliers_successfully() {
        //Arrest
        long id = 1L;
        when(supplierRepository.findById(id)).thenReturn(Optional.of(this.supplier));
        List<Supplier> suppliers = List.of(this.supplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);
        //Act
        List<Supplier> result = supplierService.getSuppliers();
        //Assert
        assertNotNull(result);
        assertEquals(result.size(), 1);
        verify(supplierService, times(1)).getSuppliers();
        
    }

    @Test
    void createSupplier_Successfully() {
        //Arrest
        when(supplierRepository.save(any(Supplier.class))).thenReturn(this.supplier);
        //Act
        Supplier result = supplierService.createSupplier(this.supplier);
        //Assert
        assertNotNull(result);
        assertEquals(result.getName(), this.supplier.getName());
        verify(supplierService, times(1)).createSupplier(this.supplier);
    }

    @Test
    void createSupplier_WhenNameAlreadyExists() {
        //Arrest
        when(supplierRepository.findByCompanyName(any(String.class))).thenReturn(Optional.of(this.supplier));
        //Act
        EntityExistsException exceptionResult = assertThrows(EntityExistsException.class, () ->
                supplierService.createSupplier(this.supplier));
        //Assert
        assertEquals("Supplier with name "+this.supplier.getName()+" already exists", exceptionResult.getMessage());
        verify(supplierService, times(1)).createSupplier(this.supplier);
    }

    @Test
    void createSupplier_WhenEmailAlreadyExists() {
        //Arrest
        when(supplierRepository.findByEmail(any(String.class))).thenReturn(Optional.of(this.supplier));
        //Act
        EntityExistsException exceptionResult = assertThrows(EntityExistsException.class, () ->
                supplierService.createSupplier(this.supplier));
        //Assert
        assertEquals("Supplier with email "+this.supplier.getEmail()+" already exists", exceptionResult.getMessage());
        verify(supplierService, times(1)).createSupplier(this.supplier);
    }

    @Test
    void updateSupplier_WhenNameAlreadyExists() {
        //Arrest
        when(supplierRepository.findByCompanyName(any(String.class))).thenReturn(Optional.of(this.supplier));
        //Act
        EntityExistsException exceptionResult = assertThrows(EntityExistsException.class, () ->
                supplierService.createSupplier(this.supplier));
        //Assert
        assertEquals("Supplier with name "+this.supplier.getName()+" already exists", exceptionResult.getMessage());
        verify(supplierService, times(1)).createSupplier(this.supplier);
    }

    @Test
    void updateSupplier_WhenEmailAlreadyExists() {
        //Arrest
        when(supplierRepository.findByEmail(any(String.class))).thenReturn(Optional.of(this.supplier));
        //Act
        EntityExistsException exceptionResult = assertThrows(EntityExistsException.class, () ->
                supplierService.createSupplier(this.supplier));
        //Assert
        assertEquals("Supplier with email "+this.supplier.getEmail()+" already exists", exceptionResult.getMessage());
        verify(supplierService, times(1)).createSupplier(this.supplier);
    }

    @Test
    void updateSupplier() {
        //Arrest
        long supplierId = 1L;
        Supplier supplierDto = Supplier.builder()
                .name("updated name")
                .email("updatedemail@gmail.com")
                .address("updated address")
                .build();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(this.supplier));
        when(supplierRepository.findByCompanyName(any(String.class))).thenReturn(Optional.empty());
        when(supplierRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(supplierRepository.save(any(Supplier.class))).thenReturn(this.supplier);
        //Act
        supplierService.updateSupplier(supplierId, supplierDto);
        //Assert
        assertEquals(supplierDto.getName(), this.supplier.getName());
        assertEquals(supplierDto.getEmail(), this.supplier.getEmail());
        assertEquals(supplierDto.getAddress(), this.supplier.getAddress());
        verify(supplierService, times(1)).updateSupplier(supplierId, supplierDto);
    }

    @Test
    void deleteSupplier() {
        //Arrest
        long supplierId = 1L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(this.supplier));
        //Act
        supplierService.deleteSupplier(supplierId);
        //Assert
        assertTrue(supplierRepository.findById(supplierId).isPresent());
        verify(supplierService, times(1)).deleteSupplier(supplierId);
    }

}
package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.models.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SupplierRepositoryTest {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    SupplierRepositoryTest(SupplierRepository supplierRepository, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @BeforeEach
    void setUp() {
        //create supplier
        Supplier supplier = Supplier.builder()
                .name("supplier")
                .email("test@test.com")
                .address("address")
                .build();
        supplierRepository.save(supplier);

        //Create category
        ProductCategory productCategory = ProductCategory.builder()
                .name("test category")
                .description("test")
                .color("Blue")
                .build();
        productCategoryRepository.save(productCategory);

        //create product
        Product product = Product.builder()
                .name("test")
                .productCategory(productCategory)
                .purchasePrice(100)
                .buyoutPrice(80)
                .supplier(supplier)
                .build();
        productRepository.save(product);
    }

    @Test
    void findByCompanyName_FindSuccessfully() {
        //Act
        Supplier supplier = supplierRepository.findByCompanyName("supplier").get();
        //Assert
        assertNotNull(supplier);
        assertEquals("supplier", supplier.getName());
    }

    @Test
    void findByEmail_successfully() {
        //Act
        Supplier supplier = supplierRepository.findByEmail("test@test.com").get();
        //Assert
        assertNotNull(supplier);
        assertEquals("supplier", supplier.getName());
    }

    @Test
    void findById_successfully() {
        //Act
        long id = supplierRepository.findAll().getFirst().getId();
        Supplier supplier = supplierRepository.findById(id).get();
        //Assert
        assertNotNull(supplier);
        assertEquals("supplier", supplier.getName());
    }

    @Test
    void findAll_successfully() {
        //Act
        long id = 1L;
        List<Supplier> suppliers = supplierRepository.findAll();
        //Assert
        assertNotNull(suppliers);
        assertEquals(1, suppliers.size());
    }

    @Test
    void delete_successfully() {
        //Act
        long id = 1L;
        supplierRepository.deleteById(id);
        //Assert
        assertFalse(supplierRepository.findById(id).isPresent());
    }
}
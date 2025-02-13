package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    private final RoleRepository roleRepository;

    @Autowired
    OrderRepositoryTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    void setUp() {
        //Products
        Product productOne = new Product(1L, "ProductOne", 100.0, 100);
        Product productTwo = new Product(1L, "ProductTwo", 200.0, 50);
        //Date of orders
        LocalDateTime orderDateOne = LocalDateTime.of(2025,2,13, 10, 20);
        LocalDateTime orderDateTwo = LocalDateTime.of(2025,2,5, 9, 20);
        //Orders
        Order orderOne = new Order(1L, productOne, 2, 200, Order.Status.ORDERED, orderDateOne);
        Order orderTwo = new Order(1L, productTwo, 5, 1000, Order.Status.ORDERED, orderDateTwo);
    }

    @Test
    void findByCostBetween() {
        //Act


    }
}
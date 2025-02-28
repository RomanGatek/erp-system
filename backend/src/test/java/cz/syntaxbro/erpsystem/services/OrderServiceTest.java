package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.services.impl.OrderServiceImpl; // Předpokládáme, že máte implementaci
import cz.syntaxbro.erpsystem.services.ProductService; // Přidejte import pro ProductService
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Předpokládáme, že máte repozitář

    @Mock
    private ProductService productService; // Přidejte mock pro ProductService

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, null, 5, 100.0, Order.Status.ORDERED, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);
        assertEquals(order, foundOrder);
    }

    @Test
    public void testGetOrders() {
        List<Order> orders = Collections.singletonList(new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrders();
        assertEquals(orders, foundOrders);
    }

    @Test
    public void testCreatedOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(5); // Nastavte platné hodnoty
        orderRequest.setCost(100.0); // Nastavte platné hodnoty

        Product product = new Product(1L, "New Product", 22.2, "Description");
        when(productService.isExistById(product.getId())).thenReturn(true); // Mockování metody isExistById
        when(orderRepository.save(any(Order.class))).thenReturn(new Order(1L, product, 5, 100.0, Order.Status.ORDERED, LocalDateTime.now())); // Mockování uložení objednávky

        Order result = orderService.createdOrder(orderRequest);
        assertEquals(product, result.getProduct());
    }
} 
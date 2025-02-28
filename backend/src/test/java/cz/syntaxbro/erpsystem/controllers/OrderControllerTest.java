package cz.syntaxbro.erpsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.OrderRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.OrderRequest;
import cz.syntaxbro.erpsystem.services.OrderService;
import cz.syntaxbro.erpsystem.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Product product;
    private Order order;
    private LocalDateTime now;
    private String dateNow;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        this.now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.dateNow = now.format(formatter);
        this.product = new Product(1L, "Product Name", 12.12, "Product Description");
        this.order = new Order(1L, product, 100, 100d, Order.Status.PREORDER, LocalDateTime.now());

    }

    /**
        Test case: Attempting to create a new product.
        Expected result: HTTP 201.
     */
    @Test
    @WithMockUser(username = "admin")
    void createProductOkTest() throws Exception {
        when(orderService.createdOrder(any(OrderRequest.class)))
                .thenReturn(this.order);

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(orderService, times(1)).createdOrder(any(OrderRequest.class));
    }

    /**
     * Test case: Attempting to create a new product with bad product id.
     * Expected result: HTTP 400.
     */
    @Test
    @WithMockUser(username = "admin")
    void createProductWrongProductNullTest() throws Exception {
        when(orderService.createdOrder(any(OrderRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID must be greater than 0"));

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(orderService, times(0)).createdOrder(any(OrderRequest.class));
    }

    /**
     * Test case: Attempting to create a new product with bad product id 0.
     * Expected result: HTTP 400.
     */
    @Test
    @WithMockUser(username = "admin")
    void createOrderWithWrongProductID0Test() throws Exception {
        this.order.setProduct(new Product(2L, "Product Name", 12.12, "Product Description"));
        when(orderService.createdOrder(any(OrderRequest.class)))
                .thenReturn(this.order);

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("Product ID must be greater than 0")))
                .andExpect(status().isBadRequest());

        verify(orderService, times(0)).createdOrder(any(OrderRequest.class));
    }

    /**
     * Test case: Attempting to get order by id 1.
     * Expected result: HTTP 200.
     */
    @Test
    @WithMockUser(username = "admin")
    void getOrderByIdBadRequestTest() throws Exception {
        // Mocking the service to return an order
        Mockito.when(orderService.getOrderById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist"));

        // Performing a request with an invalid order ID (non-existent or bad value)
        mockMvc.perform(get("/api/orders/by-product/1") // Passing an invalid ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expecting a bad request response
                .andExpect(jsonPath("$.message").value("Product does not exist")); // Assuming the exception message is "Invalid order id"
    }

    /**
     * Test case: Attempting to get order by id 1.
     * Expected result: HTTP 401.
     */
    @Test
    @WithMockUser(username = "admin")
    void getOrderByIdWithNotFound() throws Exception {
        // Mock the service to throw a 404 exception
        Mockito.when(orderService.getOrderById(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Simulate the GET request and expect 404
        mockMvc.perform(get("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found"));

        // Verify that the service was called with the correct ID
        Mockito.verify(orderService).getOrderById(1L);
    }

    @Test
    @WithMockUser(username = "admin")
    void getOrdersWithCostBetweenGoodRequest() throws Exception {
        // Mock the service to throw a 404 exception
        Mockito.when(orderService.getOrdersByCostBetween(100, 500))
                        .thenReturn(List.of(this.order));

        // Simulate the GET request and expect 404
        mockMvc.perform(get("/api/orders/cost-between?start=100&end=500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].product").value(this.product))
                .andExpect(jsonPath("$[0].status").value(Order.Status.ORDERED.toString()))
                .andExpect(jsonPath("$[0].orderTime").value(this.dateNow));

        // Verify that the service was called with the correct ID
        Mockito.verify(orderService).getOrdersByCostBetween(100, 500);
    }

    @Test
    @WithMockUser(username = "admin")
    void getOrdersWithCostBetweenBadRequest() throws Exception {
        // Mock the service to throw a 404 exception
        Mockito.when(orderService.getOrdersByCostBetween(500, 100))
                .thenThrow( new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cost must be grater or equal with 0"));

        // Simulate the GET request and expect 404
        mockMvc.perform(get("/api/orders/cost-between?start=500&end=100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cost must be grater or equal with 0"));
        // Verify that the service was called with the correct ID
        Mockito.verify(orderService).getOrdersByCostBetween(500, 100);
    }

    @Test
    @WithMockUser(username = "admin")
    void getOrdersWithDateBetweenGoodRequest() throws Exception {
        Mockito.when(orderService.getOrdersByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(this.order));
        mockMvc.perform(get("/api/orders/date-between?start=2025-02-01T23:59:59&end=2025-04-21T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].product").value(this.product))
                .andExpect(jsonPath("$[0].status").value(Order.Status.ORDERED.toString()))
                .andExpect(jsonPath("$[0].orderTime").value(this.dateNow));

        Mockito.verify(orderService).getOrdersByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void getOrdersWithDateBetweenBadRequest() throws Exception {
        Mockito.when(orderService.getOrdersByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "End must be greater or equal than start"));
        mockMvc.perform(get("/api/orders/date-between?start=2025-02-01T23:59:59&end=2025-02-01T23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("End must be greater or equal than start"));
        Mockito.verify(orderService).getOrdersByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void getOrdersWithProductIdGoodRequest() throws Exception {
        Mockito.when(orderService.getOrdersByProduct(1L))
                        .thenReturn(List.of(this.order));
        mockMvc.perform(get("/api/orders/by-product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].product").value(this.product))
                .andExpect(jsonPath("$[0].status").value(Order.Status.ORDERED.toString()))
                .andExpect(jsonPath("$[0].orderTime").value(this.dateNow));
        Mockito.verify(orderService).getOrdersByProduct(1L);
    }


    @Test
    @WithMockUser(username = "admin")
    void deleteOrder() throws Exception {
        Mockito.doNothing().when(orderService).deleteOrder(1L);
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());
        Mockito.verify(orderService).deleteOrder(1L);
    }

    @Test
    @WithMockUser(username = "admin")
    void updateOrder() throws Exception {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStatus(Order.Status.ORDERED);
        orderRequest.setOrderTime(this.order.getOrderTime());
        orderRequest.setCost(this.order.getCost());
        orderRequest.setProductId(this.product.getId());
        orderRequest.setAmount(this.order.getAmount());


        Mockito.doNothing().when(orderService).updateOrder(1L, orderRequest);
        mockMvc.perform(put("/api/orders/1"))
                .andExpect(status().isOk());
        Mockito.verify(orderService).deleteOrder(1L);
    }

}
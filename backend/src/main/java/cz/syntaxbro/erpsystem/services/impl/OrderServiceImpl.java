package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.services.OrderService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order getOrderById(int id) {
        return null;
    }

    @Override
    public List<Order> getOrders() {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByCostBetween(double start, double end) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByDateBetween(Date start, Date end) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByProduct(Product product) {
        return List.of();
    }

    @Override
    public void createOrder(Order order) {

    }

    @Override
    public void updateOrder(Order order) {

    }

    @Override
    public void deleteOrder(long id) {

    }
}

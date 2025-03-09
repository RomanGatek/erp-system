package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.responses.reports.OrderApprovalReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductPurchaseReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.ProductSalesReportDTO;
import cz.syntaxbro.erpsystem.responses.reports.SalesReportDTO;
import cz.syntaxbro.erpsystem.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementation of the ReportService interface.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ReportServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    private OrderReportTable getOrders(LocalDate startDate, LocalDate endDate) {
        // Set default dates if not provided
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        // Fetch orders within the date range
        var orders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderTime().toLocalDate();
                    return !orderDate.isBefore(effectiveStartDate) && !orderDate.isAfter(effectiveEndDate);
                })
                .toList();

        return new OrderReportTable(effectiveStartDate, effectiveEndDate, orders);
    }

    private static class OrderReportTable {
        LocalDate effectiveStartDate;
        LocalDate effectiveEndDate;
        List<Order> orders;

        public OrderReportTable(LocalDate effectiveStartDate, LocalDate effectiveEndDate, List<Order> orders) {
            this.effectiveStartDate = effectiveStartDate;
            this.effectiveEndDate = effectiveEndDate;
            this.orders = orders;
        }
    }


    @Override
    public SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate, Order.OrderType orderType) {
        OrderReportTable orderReportTable = getOrders(startDate, endDate);
        
        // Calculate metrics
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order order : orderReportTable.orders) {
            totalSales = totalSales.add(BigDecimal.valueOf(order.getTotal(orderType)));
        }
        
        int orderCount = (int) orderReportTable.orders.stream().filter(o -> o.getOrderType().equals(orderType)).count();
        
        BigDecimal averageOrderValue = orderCount > 0 
                ? totalSales.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
        
        long daysBetween = ChronoUnit.DAYS.between(orderReportTable.effectiveStartDate, orderReportTable.effectiveEndDate) + 1;
        BigDecimal dailyAverageSales = daysBetween > 0 
                ? totalSales.divide(new BigDecimal(daysBetween), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
        
        return new SalesReportDTO(
                orderReportTable.effectiveStartDate,
                orderReportTable.effectiveEndDate,
                totalSales,
                orderCount,
                averageOrderValue,
                dailyAverageSales
        );
    }

    @Override
    public List<ProductSalesReportDTO> generateBestSellingProductsReport(LocalDate startDate, LocalDate endDate, int limit) {
        OrderReportTable orderReportTable = getOrders(startDate, endDate);
        
        // Calculate total sales for the period
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order order : orderReportTable.orders) {
            totalSales = totalSales.add(BigDecimal.valueOf(order.getTotal(Order.OrderType.SELL)));
        }
        
        // Collect all order items from these orders
        List<OrderItem> allOrderItems = new ArrayList<>();
        for (Order order : orderReportTable.orders) {
            List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                    .filter(item -> item.getOrder().getId().equals(order.getId()))
                    .toList();
            allOrderItems.addAll(orderItems);
        }
        
        // Group by product and calculate metrics
        Map<Long, List<OrderItem>> itemsByProduct = new HashMap<>();
        for (OrderItem item : allOrderItems) {
            Long productId = item.getId();
            if (!itemsByProduct.containsKey(productId)) {
                itemsByProduct.put(productId, new ArrayList<>());
            }
            itemsByProduct.get(productId).add(item);
        }
        
        List<ProductSalesReportDTO> productReports = new ArrayList<>();
        
        for (Map.Entry<Long, List<OrderItem>> entry : itemsByProduct.entrySet()) {
            Long productId = entry.getKey();
            List<OrderItem> items = entry.getValue();
            
            // Get the product
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                continue;
            }
            Product product = productOpt.get();
            
            // Calculate metrics
            int quantitySold = 0;
            for (OrderItem item : items) {
                quantitySold += item.getQuantity();
            }


            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (OrderItem item : items) {
                Product product_ = item.getInventoryItem().getProduct();
                BigDecimal itemPrice = BigDecimal.valueOf(item.getQuantity() * product_.getBuyoutPrice());
                totalRevenue = totalRevenue.add(itemPrice);
            }

            BigDecimal percentageOfTotalSales = totalSales.compareTo(BigDecimal.ZERO) > 0
                    ? totalRevenue.multiply(new BigDecimal(100)).divide(totalSales, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            
            productReports.add(new ProductSalesReportDTO(
                    productId,
                    product.getName(),
                    product.getDescription(),
                    quantitySold,
                    totalRevenue,
                    BigDecimal.valueOf(product.getBuyoutPrice()),
                    percentageOfTotalSales
            ));
        }
        
        // Sort by total revenue in descending order and limit results
        productReports.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));
        if (productReports.size() > limit) {
            productReports = productReports.subList(0, limit);
        }
        
        return productReports;
    }

    @Override
    public List<ProductPurchaseReportDTO> generateMostPurchasedProductsReport(LocalDate startDate, LocalDate endDate, int limit) {
        // Set default dates if not provided
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        // Fetch inventory items with purchase dates in the range
        List<InventoryItem> inventoryItems = inventoryRepository.findAll().stream()
                .filter(item -> {
                    LocalDate purchaseDate = item.getCreatedAt().toLocalDate();
                    return !purchaseDate.isBefore(effectiveStartDate) && !purchaseDate.isAfter(effectiveEndDate);
                })
                .toList();

        // Group by product and calculate metrics
        Map<Long, List<InventoryItem>> itemsByProduct = new HashMap<>();
        for (InventoryItem item : inventoryItems) {
            Long productId = item.getProduct().getId();
            if (!itemsByProduct.containsKey(productId)) {
                itemsByProduct.put(productId, new ArrayList<>());
            }
            itemsByProduct.get(productId).add(item);
        }
        
        List<ProductPurchaseReportDTO> productReports = new ArrayList<>();
        
        for (Map.Entry<Long, List<InventoryItem>> entry : itemsByProduct.entrySet()) {
            Long productId = entry.getKey();
            List<InventoryItem> items = entry.getValue();
            
            // Get the product
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                continue;
            }
            Product product = productOpt.get();
            
            // Calculate metrics
            int quantityPurchased = 0;
            for (InventoryItem item : items) {
                quantityPurchased += item.getStockedAmount();
            }
            
            BigDecimal totalCost = BigDecimal.ZERO;
            for (InventoryItem item : items) {
                totalCost = totalCost.add(BigDecimal.valueOf(item.getProduct().getPurchasePrice() * item.getStockedAmount()));
            }
            
            BigDecimal averagePurchasePrice = quantityPurchased > 0
                    ? totalCost.divide(new BigDecimal(quantityPurchased), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            
            productReports.add(new ProductPurchaseReportDTO(
                    productId,
                    product.getName(),
                    product.getDescription(),
                    quantityPurchased,
                    totalCost,
                    averagePurchasePrice,
                    items.size()
            ));
        }
        
        // Sort by quantity purchased in descending order and limit results
        productReports.sort((a, b) -> Integer.compare(b.getQuantityPurchased(), a.getQuantityPurchased()));
        if (productReports.size() > limit) {
            productReports = productReports.subList(0, limit);
        }
        
        return productReports;
    }

    @Override
    public List<OrderApprovalReportDTO> generateOrderApprovalsReport(LocalDate startDate, LocalDate endDate) {
        // Set default dates if not provided
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        Order.Status status = Order.Status.CONFIRMED;

        // Fetch approved orders within the date range
        List<Order> approvedOrders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderTime().toLocalDate();
                    return status.equals(order.getStatus()) &&
                           !orderDate.isBefore(effectiveStartDate) &&
                           !orderDate.isAfter(effectiveEndDate);
                })
                .toList();

        // Calculate total number of approvals
        int totalApprovals = approvedOrders.size();

        // Group by approver and calculate metrics
        Map<Long, List<Order>> ordersByApprover = new HashMap<>();
        for (Order order : approvedOrders) {
            if (order.getApprovedBy() == null) continue;

            Long approverId = order.getApprovedBy().getId();
            if (!ordersByApprover.containsKey(approverId)) {
                ordersByApprover.put(approverId, new ArrayList<>());
            }
            ordersByApprover.get(approverId).add(order);
        }

        List<OrderApprovalReportDTO> approvalReports = new ArrayList<>();

        for (Map.Entry<Long, List<Order>> entry : ordersByApprover.entrySet()) {
            Long userId = entry.getKey();
            List<Order> orders = entry.getValue();

            // Get the user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                continue;
            }
            User user = userOpt.get();

            // Calculate metrics
            int approvedOrderCount = orders.size();

            BigDecimal totalApprovedAmount = BigDecimal.ZERO;
            for (Order order : orders) {
                totalApprovedAmount = totalApprovedAmount.add(order.getTotal());
            }

            BigDecimal averageApprovedOrderAmount = approvedOrderCount > 0
                    ? totalApprovedAmount.divide(new BigDecimal(approvedOrderCount), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            double percentageOfTotalApprovals = (double) approvedOrderCount / totalApprovals * 100;

            approvalReports.add(new OrderApprovalReportDTO(
                    userId,
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName(),
                    approvedOrderCount,
                    totalApprovedAmount,
                    averageApprovedOrderAmount,
                    percentageOfTotalApprovals
            ));
        }

        // Sort by approved order count in descending order
        approvalReports.sort((a, b) -> Integer.compare(b.getApprovedOrderCount(), a.getApprovedOrderCount()));

        return approvalReports;
    }
} 
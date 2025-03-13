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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private List<Order> getOrders(LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveStartDate = (startDate != null) ? startDate : LocalDate.now().minusMonths(1);
        LocalDate effectiveEndDate = (endDate != null) ? endDate : LocalDate.now().plusMonths(1);

        return orderRepository.findAllByOrderTimeBetween(
                effectiveStartDate.atStartOfDay(),
                effectiveEndDate.atTime(23, 59, 59)
        );
    }


    @Override
    public SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate, Order.OrderType orderType) {
        List<Order> orders = getOrders(startDate, endDate).stream()
                .filter(order -> order.getOrderType().equals(orderType))
                .toList();

        BigDecimal totalSales = orders.stream()
                .map(order -> BigDecimal.valueOf(order.getTotal(orderType)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int orderCount = orders.size();

        BigDecimal averageOrderValue = orderCount > 0
                ? totalSales.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal dailyAverageSales = daysBetween > 0
                ? totalSales.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new SalesReportDTO(startDate, endDate, totalSales, orderCount, averageOrderValue, dailyAverageSales);
    }

    @Override
    public List<ProductSalesReportDTO> generateBestSellingProductsReport(LocalDate startDate, LocalDate endDate, int limit) {
        List<Order> orders = getOrders(startDate, endDate);

        Set<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toSet());
        List<OrderItem> allOrderItems = orderItemRepository.findByOrderIdIn(orderIds);

        BigDecimal totalSales = orders.stream()
                .map(order -> BigDecimal.valueOf(order.getTotal(Order.OrderType.SELL)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<OrderItem>> itemsByProduct = allOrderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getInventoryItem().getProduct().getId()));

        Map<Long, Product> productsMap = productRepository.findAllById(itemsByProduct.keySet()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return itemsByProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<OrderItem> items = entry.getValue();
                    Product product = productsMap.get(productId);
                    if (product == null) return null;

                    int quantitySold = items.stream().mapToInt(OrderItem::getQuantity).sum();
                    BigDecimal totalRevenue = items.stream()
                            .map(item -> BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(product.getBuyoutPrice())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percentageOfTotalSales = totalSales.compareTo(BigDecimal.ZERO) > 0
                            ? totalRevenue.multiply(BigDecimal.valueOf(100)).divide(totalSales, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new ProductSalesReportDTO(productId, product.getName(), product.getDescription(), quantitySold, totalRevenue,
                            BigDecimal.valueOf(product.getBuyoutPrice()), percentageOfTotalSales);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ProductSalesReportDTO::getTotalRevenue).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<ProductPurchaseReportDTO> generateMostPurchasedProductsReport(LocalDate startDate, LocalDate endDate, int limit) {
        List<InventoryItem> inventoryItems = inventoryRepository.findAllByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<Long, List<InventoryItem>> itemsByProduct = inventoryItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getId()));

        Map<Long, Product> productsMap = productRepository.findAllById(itemsByProduct.keySet()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return itemsByProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<InventoryItem> items = entry.getValue();
                    Product product = productsMap.get(productId);
                    if (product == null) return null;

                    int quantityPurchased = items.stream().mapToInt(InventoryItem::getStockedAmount).sum();
                    BigDecimal totalCost = items.stream()
                            .map(item -> BigDecimal.valueOf(item.getStockedAmount()).multiply(BigDecimal.valueOf(product.getPurchasePrice())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal averagePurchasePrice = quantityPurchased > 0
                            ? totalCost.divide(BigDecimal.valueOf(quantityPurchased), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new ProductPurchaseReportDTO(productId, product.getName(), product.getDescription(), quantityPurchased,
                            totalCost, averagePurchasePrice, items.size());
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ProductPurchaseReportDTO::getQuantityPurchased).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<OrderApprovalReportDTO> generateOrderApprovalsReport(LocalDate startDate, LocalDate endDate) {
        List<Order> approvedOrders = orderRepository.findAllByStatusAndOrderTimeBetween(Order.Status.CONFIRMED,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        int totalApprovals = approvedOrders.size();

        return approvedOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getApprovedBy().getId(), Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<Order> orders = entry.getValue();
                    User user = userRepository.findById(userId).orElse(null);
                    if (user == null) return null;

                    BigDecimal totalApprovedAmount = orders.stream()
                            .map(Order::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new OrderApprovalReportDTO(userId, user.getUsername(), user.getFirstName() + " " + user.getLastName(),
                            orders.size(), totalApprovedAmount, totalApprovedAmount.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP),
                            (double) orders.size() / totalApprovals * 100);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(OrderApprovalReportDTO::getApprovedOrderCount).reversed())
                .toList();
    }
}

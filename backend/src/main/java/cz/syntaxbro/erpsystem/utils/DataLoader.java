package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author steve
 * Class to create dummy permission, role and user data
 */

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordSecurity encoder;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      PermissionRepository permissionRepository, PasswordSecurity encoder,
                      ProductRepository productRepository, InventoryRepository inventoryRepository,
                      OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                      ProductCategoryRepository productCategoryRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.encoder = encoder;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Create permissions and roles
        Permission readReports = createPermissionIfNotExists("READ_REPORTS");
        Permission approveBudgets = createPermissionIfNotExists("APPROVE_BUDGETS");
        Permission viewProfile = createPermissionIfNotExists("VIEW_PROFILE");

        Role adminRole = createRoleIfNotExists("ROLE_ADMIN", Set.of(readReports, approveBudgets));
        Role managerRole = createRoleIfNotExists("ROLE_MANAGER", Set.of(readReports));
        Role userRole = createRoleIfNotExists("ROLE_USER", Set.of(viewProfile));

        // Create users
        createUserIfNotExists(
                "administrator",
                "_0",
                "admin@example.com",
                Set.of(adminRole, userRole, managerRole)
        );
        createUserIfNotExists(
                "manager",
                "_1",
                "manager@example.com",
                Set.of(managerRole, userRole)
        );
        createUserIfNotExists(
                "user",
                "_2",
                "user@example.com",
                Set.of(userRole)
        );

        createProductCategoryIfNotExists();
        @SuppressWarnings("OptionalGetWithoutIsPresent") ProductCategory defaultCategory = productCategoryRepository.findByName("default").get();

        // Create sample products
        createProductIfNotExists("Milka Chocolate 200g", 52.2, 52.2, "Milk chocolate bar", defaultCategory);
        createProductIfNotExists("Black Tea", 22.2, 22.2, "Premium black tea", defaultCategory);
        createProductIfNotExists("Semi-skimmed Milk", 19.90, 19.90, "Fresh dairy milk", defaultCategory);
        createProductIfNotExists("Rye Bread", 34.5, 34.5, "Fresh baked bread", defaultCategory);
        createProductIfNotExists("Butter 250g", 49.9, 49.9, "Farm butter", defaultCategory);
        createProductIfNotExists("Apples 1kg", 29.99, 29.99, "Fresh local apples", defaultCategory);
        createProductIfNotExists("Bananas 1kg", 26.5, 26.5, "Ripe bananas", defaultCategory);
        createProductIfNotExists("Beef Steak 300g", 159.0, 159.0, "Premium beef", defaultCategory);
        createProductIfNotExists("Chicken Breast 500g", 89.9, 89.9, "Fresh chicken", defaultCategory);
        createProductIfNotExists("Bread Roll", 3.5, 3.5, "Crispy roll", defaultCategory);
        createProductIfNotExists("Edam Cheese 100g", 24.0, 24.0, "Mild cheese", defaultCategory);
        createProductIfNotExists("Ham 100g", 32.9, 32.9, "Quality ham", defaultCategory);
        createProductIfNotExists("Oranges 1kg", 45.0, 45.0, "Juicy oranges", defaultCategory);
        createProductIfNotExists("Coffee Beans 250g", 149.0, 149.0, "Strong coffee", defaultCategory);
        createProductIfNotExists("Spaghetti 500g", 32.5, 32.5, "Italian pasta", defaultCategory);
        createProductIfNotExists("Basmati Rice 1kg", 69.0, 69.0, "Aromatic rice", defaultCategory);
        createProductIfNotExists("Olive Oil 500ml", 129.0, 129.0, "Extra virgin", defaultCategory);
        createProductIfNotExists("Ketchup 500g", 39.9, 39.9, "Tomato ketchup", defaultCategory);
        createProductIfNotExists("Mustard 200g", 19.9, 19.9, "Traditional mustard", defaultCategory);
        createProductIfNotExists("Chocolate Bar", 14.5, 14.5, "Sweet treat", defaultCategory);
        createProductIfNotExists("Potatoes 2kg", 49.9, 49.9, "Local potatoes", defaultCategory);
        createProductIfNotExists("Onions 1kg", 22.9, 22.9, "Quality onions", defaultCategory);
        createProductIfNotExists("Garlic 200g", 29.5, 29.5, "Aromatic garlic",defaultCategory);
        createProductIfNotExists("Strawberry Yogurt 150g", 15.9, 15.9, "Creamy yogurt", defaultCategory);
        createProductIfNotExists("Toilet Paper 8pcs", 89.9, 89.9, "Soft and strong", defaultCategory);


        // Create inventory items for sample products
        Product milkaProduct = productRepository.findByName("Milka Chocolate 200g")
                .orElseThrow(() -> new RuntimeException("Milka Chocolate 200g not found"));
        Product teaProduct = productRepository.findByName("Black Tea")
                .orElseThrow(() -> new RuntimeException("Black Tea not found"));
        Product milkProduct = productRepository.findByName("Semi-skimmed Milk")
                .orElseThrow(() -> new RuntimeException("Semi-skimmed Milk not found"));
        Product onionsProduct = productRepository.findByName("Onions 1kg")
                .orElseThrow(() -> new RuntimeException("Onions 1kg not found"));
        Product toiledPaperProduct = productRepository.findByName("Toilet Paper 8pcs")
                .orElseThrow(() -> new RuntimeException("Toileet Paper 8pcs not found"));


        createInventoryItemIfNotExists(milkaProduct, 50);
        createInventoryItemIfNotExists(teaProduct, 120);
        createInventoryItemIfNotExists(milkProduct, 200);
        createInventoryItemIfNotExists(onionsProduct, 400);
        createInventoryItemIfNotExists(toiledPaperProduct, 500);

        createSampleOrders();
        createProductTriggers();
    }

    @Transactional
    public void createProductTriggers() {
        // 1️⃣ Vytvoření logovací tabulky
        entityManager.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS trigger_logs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                trigger_name VARCHAR(100),
                executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                message VARCHAR(255)
            );
        """).executeUpdate();

        // 2️⃣ Odstranění starého triggeru before_product_delete
        entityManager.createNativeQuery("DROP TRIGGER IF EXISTS before_product_delete;").executeUpdate();

        // 3️⃣ Vytvoření triggeru before_product_delete
        entityManager.createNativeQuery("""
            CREATE TRIGGER before_product_delete
            BEFORE DELETE ON products FOR EACH ROW
            BEGIN
                UPDATE orders o
                SET cost = (
                    SELECT IFNULL(SUM(oi.quantity *
                                      IF(o.order_type = 'SELL', p.buyout_price, p.purchase_price)), 0)
                    FROM order_items oi
                             JOIN inventory_items ii ON oi.inventory_item = ii.id
                             JOIN products p ON ii.product_id = p.id
                    WHERE oi.order_id = o.id AND p.id <> OLD.id
                )
                WHERE o.id IN (
                    SELECT DISTINCT oi.order_id
                    FROM order_items oi
                             JOIN inventory_items ii ON oi.inventory_item = ii.id
                    WHERE ii.product_id = OLD.id
                );

                INSERT INTO trigger_logs (trigger_name, message, executed_at)
                VALUES ('before_product_delete', CONCAT('Trigger executed before deleting product id=', OLD.id), NOW());
            END;
        """).executeUpdate();

        // 4️⃣ Odstranění starého triggeru after_order_item_delete
        entityManager.createNativeQuery("DROP TRIGGER IF EXISTS after_order_item_delete;").executeUpdate();

        // 5️⃣ Vytvoření triggeru after_order_item_delete
        entityManager.createNativeQuery("""
            CREATE TRIGGER after_order_item_delete
            AFTER DELETE ON order_items FOR EACH ROW
            BEGIN
                UPDATE orders o
                SET cost = (
                    SELECT IFNULL(SUM(oi.quantity *
                                      IF(o.order_type = 'SELL', p.buyout_price, p.purchase_price)), 0)
                    FROM order_items oi
                             JOIN inventory_items ii ON oi.inventory_item = ii.id
                             JOIN products p ON ii.product_id = p.id
                    WHERE oi.order_id = OLD.order_id
                )
                WHERE o.id = OLD.order_id;

                INSERT INTO trigger_logs (trigger_name, message, executed_at)
                VALUES ('after_order_item_delete', CONCAT('Trigger executed for order_id=', OLD.order_id), NOW());
            END;
        """).executeUpdate();
    }

    /**
     * Creates several sample orders with different statuses for workflow testing
     */
    private void createSampleOrders() {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            ErpSystemApplication.getLogger().warn("[DATA LOADER] No products found for creating orders");
            return;
        }

        if (orderRepository.count() > 0) {
            ErpSystemApplication.getLogger().info("[DATA LOADER] Orders already exist, skipping sample order creation");
            return;
        }

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        User admin = userRepository.findByUsername("administrator")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        User manager = userRepository.findByUsername("manager")
                .orElseThrow(() -> new RuntimeException("Manager user not found"));

        List<InventoryItem> allItems = inventoryRepository.findAll();

        IntStream.range(0, 20).forEach(i -> {
            var order = Order.builder()
                    .orderItems(new ArrayList<>())
                    .orderTime(now.minusDays(random.nextInt(30)))
                    .status(Order.Status.PENDING)
                    .comment("Test")
                    .approvedBy(admin)
                    .decisionTime(now.minusDays(39))
                    .build();

            int itemCount = random.nextInt(4) + 2; // 2 až 5 položek na objednávku
            double totalCost = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (int x = 0; x < itemCount; x++) {
                var inventoryItem = allItems.get(random.nextInt(allItems.size()));
                int quantity = random.nextInt(10) + 1;
                double itemCost = inventoryItem.getProduct().getBuyoutPrice() * quantity;
                totalCost += itemCost;

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .inventoryItem(inventoryItem)
                        .quantity(quantity)
                        .build();

                orderItems.add(orderItemRepository.save(orderItem));
            }

            order.setCost(totalCost);
            order.getOrderItems().addAll(orderItems);

            switch (random.nextInt(10)) {
                case 0, 1, 2, 3 -> {
                    order.setStatus(Order.Status.PENDING);
                    order.setComment("Pending approval");
                    order.setDecisionTime(null);
                    order.setOrderType(Order.OrderType.SELL);
                }
                case 4, 5, 6, 7 -> {
                    order.setStatus(Order.Status.CONFIRMED);
                    order.setComment("Order approved, product is in stock.");
                    order.setApprovedBy(admin);
                    order.setOrderType(Order.OrderType.PURCHASE);
                    order.setDecisionTime(order.getOrderTime().plusHours(random.nextInt(24) + 1));
                }
                default -> {
                    order.setStatus(Order.Status.CANCELED);
                    order.setComment("Order rejected due to low stock levels.");
                    order.setApprovedBy(manager);
                    order.setOrderType(Order.OrderType.SELL);
                    order.setDecisionTime(order.getOrderTime().plusHours(random.nextInt(12) + 1));
                }
            }

            switch (i) {
                case 0 -> {
                    order.setStatus(Order.Status.PENDING);
                    order.setOrderTime(now.minusHours(1));
                    order.setComment("New order awaiting approval");
                    order.setDecisionTime(null);
                }
                case 1 -> {
                    order.setStatus(Order.Status.CONFIRMED);
                    order.setOrderTime(now.minusHours(5));
                    order.setComment("Ship immediately, priority customer.");
                    order.setApprovedBy(admin);
                    order.setDecisionTime(now.minusMinutes(30));
                }
                case 2 -> {
                    order.setStatus(Order.Status.CANCELED);
                    order.setOrderTime(now.minusHours(8));
                    order.setComment("Product currently not available in stock.");
                    order.setApprovedBy(manager);
                    order.setDecisionTime(now.minusMinutes(15));
                }
            }

            if (totalCost > 1000) {
                order.setStatus(Order.Status.PENDING);
                order.setComment("Large order pending approval");
                order.setDecisionTime(null);
            }

            orderRepository.save(order);
        });
    }

    private void createInventoryItemIfNotExists(Product product, int quantity) {
        Optional<InventoryItem> itemFromDb = inventoryRepository.findByProduct(product);
        if (itemFromDb.isEmpty()) {
            InventoryItem inventoryItem = InventoryItem.builder()
                    .product(product)
                    .stockedAmount(quantity)
                    .createdAt(LocalDateTime.now())
                    .build();
            inventoryRepository.save(inventoryItem);
        }
    }

    private Permission createPermissionIfNotExists(String permissionName) {
        Optional<Permission> permissionFromDb = permissionRepository.findByName(permissionName);

        return permissionFromDb.orElseGet(() -> permissionRepository.save(new Permission(permissionName)));
    }

    private Role createRoleIfNotExists(String roleName, Set<Permission> permissionList) {
        Optional<Role> roleFromDb = roleRepository.findByName(roleName);

        return roleFromDb.orElseGet(() -> roleRepository.save(new Role(roleName, permissionList)));
    }

    private void createUserIfNotExists(String username, String firstName, String email, Set<Role> roles) {
        Optional<User> userFromDb = userRepository.findByEmail(email);
        if (userFromDb.isEmpty()) {

            ErpSystemApplication.getLogger().warn("[DATA LOADER] User {} fetch already exists.", username);

            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode("P&ssw0rd123@")); // Default password
            user.setFirstName(firstName);
            user.setLastName("_"); // Default last name
            user.setEmail(email);
            user.setActive(true); // Default active status
            user.setRoles(roles);

            userRepository.save(user);
        } else {
            ErpSystemApplication.getLogger().info("[DATA LOADER] User {} created.", username);
        }
    }

    private void createProductCategoryIfNotExists() {
        //create product category
        Optional<ProductCategory> productCategoryFromDb = productCategoryRepository.findByName("default");
        if (productCategoryFromDb.isEmpty()) {
            productCategoryRepository.save(ProductCategory.builder()
                    .name("default")
                    .description("default")
                    .build());
        }
    }

    private void createProductIfNotExists(String name, double buyoutPrice, double purchasePrice, String description, ProductCategory productCategory) {

        Optional<Product> productFromDB = productRepository.findByName(name);
        if (productFromDB.isEmpty()) {
            productRepository.save(
            Product.builder()
                    .name(name)
                    .purchasePrice(purchasePrice)
                    .buyoutPrice(buyoutPrice)
                    .description(description)
                    .productCategory(productCategory)
                    .build()
            );
        }
    }
}
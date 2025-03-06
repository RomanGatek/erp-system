package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      PermissionRepository permissionRepository, PasswordSecurity encoder, 
                      ProductRepository productRepository, InventoryRepository inventoryRepository,
                      OrderRepository orderRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.encoder = encoder;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
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

        // Create sample products
        createProductIfNotExists("Milka Chocolate 200g", 52.2, "Milk chocolate bar");
        createProductIfNotExists("Black Tea", 22.2, "Premium black tea");
        createProductIfNotExists("Semi-skimmed Milk", 19.90, "Fresh dairy milk");
        createProductIfNotExists("Rye Bread", 34.5, "Fresh baked bread");
        createProductIfNotExists("Butter 250g", 49.9, "Farm butter");
        createProductIfNotExists("Apples 1kg", 29.99, "Fresh local apples");
        createProductIfNotExists("Bananas 1kg", 26.5, "Ripe bananas");
        createProductIfNotExists("Beef Steak 300g", 159.0, "Premium beef");
        createProductIfNotExists("Chicken Breast 500g", 89.9, "Fresh chicken");
        createProductIfNotExists("Bread Roll", 3.5, "Crispy roll");
        createProductIfNotExists("Edam Cheese 100g", 24.0, "Mild cheese");
        createProductIfNotExists("Ham 100g", 32.9, "Quality ham");
        createProductIfNotExists("Oranges 1kg", 45.0, "Juicy oranges");
        createProductIfNotExists("Coffee Beans 250g", 149.0, "Strong coffee");
        createProductIfNotExists("Spaghetti 500g", 32.5, "Italian pasta");
        createProductIfNotExists("Basmati Rice 1kg", 69.0, "Aromatic rice");
        createProductIfNotExists("Olive Oil 500ml", 129.0, "Extra virgin");
        createProductIfNotExists("Ketchup 500g", 39.9, "Tomato ketchup");
        createProductIfNotExists("Mustard 200g", 19.9, "Traditional mustard");
        createProductIfNotExists("Chocolate Bar", 14.5, "Sweet treat");
        createProductIfNotExists("Potatoes 2kg", 49.9, "Local potatoes");
        createProductIfNotExists("Onions 1kg", 22.9, "Quality onions");
        createProductIfNotExists("Garlic 200g", 29.5, "Aromatic garlic");
        createProductIfNotExists("Strawberry Yogurt 150g", 15.9, "Creamy yogurt");
        createProductIfNotExists("Toilet Paper 8pcs", 89.9, "Soft and strong");

        // Create inventory items for sample products
        Product milkaProduct = productRepository.findByName("Milka Chocolate 200g")
                .orElseThrow(() -> new RuntimeException("Milka Chocolate 200g not found"));
        Product teaProduct = productRepository.findByName("Black Tea")
                .orElseThrow(() -> new RuntimeException("Black Tea not found"));
        Product milkProduct = productRepository.findByName("Semi-skimmed Milk")
                .orElseThrow(() -> new RuntimeException("Semi-skimmed Milk not found"));

        createInventoryItemIfNotExists(milkaProduct, 50);
        createInventoryItemIfNotExists(teaProduct, 120);
        createInventoryItemIfNotExists(milkProduct, 200);

        // Create sample orders
        createSampleOrders();
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
        
        User admin = userRepository.findByUsername("administrator").orElse(null);
        User manager = userRepository.findByUsername("manager").orElse(null);

        // Create 20 different orders
        for (int i = 0; i < 20; i++) {
            Product product = allProducts.get(random.nextInt(allProducts.size()));
            int amount = random.nextInt(15) + 1;
            double cost = product.getPrice() * amount;
            LocalDateTime orderTime = now.minusDays(random.nextInt(30));
            
            Order order = Order.builder()
                    .product(product)
                    .amount(amount)
                    .cost(cost)
                    .orderTime(orderTime)
                    .build();
            
            int statusRandom = random.nextInt(10);
            
            if (statusRandom < 4) {  // 40% pending
                order.setStatus(Order.Status.PENDING);
                
            } else if (statusRandom < 8) {  // 40% confirmed
                order.setStatus(Order.Status.CONFIRMED);
                order.setComment("Order approved, product is in stock.");
                order.setApprovedBy(admin != null ? admin.getUsername() : "admin");
                order.setDecisionTime(orderTime.plusHours(random.nextInt(24) + 1));
                
            } else {  // 20% canceled
                order.setStatus(Order.Status.CANCELED);
                order.setComment("Order rejected due to low stock levels.");
                order.setApprovedBy(manager != null ? manager.getUsername() : "manager");
                order.setDecisionTime(orderTime.plusHours(random.nextInt(12) + 1));
            }
            
            // Special cases for better testing
            if (i == 0) {  // First order always pending (new)
                order.setStatus(Order.Status.PENDING);
                order.setOrderTime(now.minusHours(1));
                order.setComment(null);
                order.setApprovedBy(null);
                order.setDecisionTime(null);
            } else if (i == 1) {  // Second order just approved
                order.setStatus(Order.Status.CONFIRMED);
                order.setOrderTime(now.minusHours(5));
                order.setComment("Ship immediately, priority customer.");
                order.setApprovedBy(admin != null ? admin.getUsername() : "admin");
                order.setDecisionTime(now.minusMinutes(30));
            } else if (i == 2) {  // Third order just rejected
                order.setStatus(Order.Status.CANCELED);
                order.setOrderTime(now.minusHours(8));
                order.setComment("Product currently not available in stock.");
                order.setApprovedBy(manager != null ? manager.getUsername() : "manager");
                order.setDecisionTime(now.minusMinutes(15));
            }
            
            // Large orders set to PENDING to be noticeable
            if (amount > 10) {
                order.setStatus(Order.Status.PENDING);
                order.setComment(null);
                order.setApprovedBy(null);
                order.setDecisionTime(null);
            }
            
            orderRepository.save(order);
            ErpSystemApplication.getLogger().info("[DATA LOADER] Created order for {} x{}, status: {}", 
                    product.getName(), amount, order.getStatus());
        }
    }

    /**
     * Metoda pro vytvoření InventoryItemu, pokud ještě neexistuje.
     * Např. klíčová kontrola je na základě produktu (1 InventoryItem = 1 Product).
     */
    private void createInventoryItemIfNotExists(Product product, int quantity) {
        Optional<InventoryItem> itemFromDb = inventoryRepository.findByProduct(product);
        if (itemFromDb.isEmpty()) {
            InventoryItem newItem = new InventoryItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            inventoryRepository.save(newItem);
        }
    }

    private Permission createPermissionIfNotExists(String permissionName) {
        Optional<Permission> permissionFromDb = permissionRepository.findByName(permissionName);

        return permissionFromDb.orElseGet(() ->
                permissionRepository.save(new Permission(permissionName))
        );
    }

    private Role createRoleIfNotExists(String roleName, Set<Permission> permissionList) {
        Optional<Role> roleFromDb = roleRepository.findByName(roleName);

        return roleFromDb.orElseGet(() ->
                roleRepository.save(new Role(roleName, permissionList))
        );
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

    private void createProductIfNotExists(
            String name,
            double price,
            String description
    ) {
        Optional<Product> productFromDB = productRepository.findByName(name);
        if (productFromDB.isEmpty()) {
            productRepository.save(
            Product.builder()
                    .name(name)
                    .price(price)
                    .description(description)
                    .build()
            );
        }
    }
}
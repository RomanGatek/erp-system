package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.*;
import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
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

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      PermissionRepository permissionRepository, PasswordSecurity encoder, ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.encoder = encoder;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Create permissions
        Permission readReports = createPermissionIfNotExists("READ_REPORTS");
        Permission approveBudgets = createPermissionIfNotExists("APPROVE_BUDGETS");
        Permission viewProfile = createPermissionIfNotExists("VIEW_PROFILE");

        // Create roles with permissions
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

        createProductIfNotExists("Milka 200g", 52.2, "čokoláda milka");
        createProductIfNotExists("Čaj černý", 22.2, "Dobrý čaj");
        createProductIfNotExists("Mléko polotučné", 19.90, "PavelMlikeš");
        createProductIfNotExists("Chléb žitný", 34.5, "Čerstvý chléb");
        createProductIfNotExists("Máslo 250g", 49.9, "Farmářské máslo");
        createProductIfNotExists("Jablka 1kg", 29.99, "Česká jablka");
        createProductIfNotExists("Banány 1kg", 26.5, "Zralé banány");
        createProductIfNotExists("Hovězí steak 300g", 159.0, "Prémiové hovězí");
        createProductIfNotExists("Kuřecí prsa 500g", 89.9, "Čerstvé kuře");
        createProductIfNotExists("Rohlík", 3.5, "Křupavý rohlík");
        createProductIfNotExists("Sýr Eidam 100g", 24.0, "Jemný sýr");
        createProductIfNotExists("Šunka 100g", 32.9, "Kvalitní šunka");
        createProductIfNotExists("Pomeranče 1kg", 45.0, "Šťavnaté pomeranče");
        createProductIfNotExists("Káva zrnková 250g", 149.0, "Silná káva");
        createProductIfNotExists("Těstoviny špagety 500g", 32.5, "Italské těstoviny");
        createProductIfNotExists("Rýže basmati 1kg", 69.0, "Voňavá rýže");
        createProductIfNotExists("Olivový olej 500ml", 129.0, "Extra panenský");
        createProductIfNotExists("Kečup 500g", 39.9, "Rajčatový kečup");
        createProductIfNotExists("Hořčice 200g", 19.9, "Tradiční hořčice");
        createProductIfNotExists("Čokoládová tyčinka", 14.5, "Sladká odměna");
        createProductIfNotExists("Brambory 2kg", 49.9, "České brambory");
        createProductIfNotExists("Cibule 1kg", 22.9, "Kvalitní cibule");
        createProductIfNotExists("Česnek 200g", 29.5, "Aromatický česnek");
        createProductIfNotExists("Jogurt jahodový 150g", 15.9, "Lahodný jogurt");
        createProductIfNotExists("Toaletní papír 8ks", 89.9, "Měkký a pevný");


        // Po vytvoření / aktualizaci produktů je získáme z DB a založíme několik InventoryItem
        Product milkaProduct = productRepository.findByName("Milka 200g")
                .orElseThrow(() -> new RuntimeException("Milka 200g not found"));
        Product cajProduct = productRepository.findByName("Čaj černý")
                .orElseThrow(() -> new RuntimeException("Čaj černý not found"));
        Product mlekoProduct = productRepository.findByName("Mléko polotučné")
                .orElseThrow(() -> new RuntimeException("Mléko polotučné not found"));

        createInventoryItemIfNotExists(milkaProduct, 50);
        createInventoryItemIfNotExists(cajProduct, 120);
        createInventoryItemIfNotExists(mlekoProduct, 200);

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
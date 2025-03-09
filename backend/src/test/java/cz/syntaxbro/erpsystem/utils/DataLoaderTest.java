package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.responses.OrderItemReponse;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test class for DataLoader.
 * Ensures the correct initialization of permissions, roles, and users.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DataLoaderTest {

    @Mock
    private PasswordSecurity passwordSecurity;

    @InjectMocks
    private DataLoader dataLoader;

    // Repositories used for testing persistence of roles, users, and permissions.
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    @Autowired
    private ProductRepository productRepository; // Directly injected by Spring

    private final InventoryRepository inventoryRepository;

    /**
     * Constructor to manually inject required repositories.
     * This ensures the repositories are properly initialized for testing.
     */
    DataLoaderTest(@Autowired RoleRepository roleRepository,
                   @Autowired UserRepository userRepository,
                   @Autowired PermissionRepository permissionRepository,
                   @Autowired InventoryRepository inventoryRepository,
                   @Autowired ProductRepository productRepository,
                   @Autowired OrderRepository orderRepository,
                   @Autowired OrderItemRepository orderItemRepository,
                   @Autowired OrderService orderService
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }

    /**
     * Setup method executed before each test.
     * - Mocks password hashing to return a predefined value.
     * - Initializes and executes the DataLoader.
     */
    @BeforeEach
    void setUp() {
        when(passwordSecurity.encode(anyString())).thenReturn("hashedPassword");

        dataLoader = new DataLoader(roleRepository, userRepository, permissionRepository, passwordSecurity, productRepository, inventoryRepository, orderRepository, orderItemRepository, orderService);
        dataLoader.run(); // Populates database with initial data.
    }

    /**
     * Test: Ensures permissions are correctly persisted in the database.
     * Expected result: Permissions should be present.
     */
    @Test
    void shouldPersistPermissions() {
        assertThat(permissionRepository.findByName("READ_REPORTS")).isPresent();
        assertThat(permissionRepository.findByName("APPROVE_BUDGETS")).isPresent();
        assertThat(permissionRepository.findByName("VIEW_PROFILE")).isPresent();
    }

    /**
     * Test: Ensures roles and their associated permissions are persisted.
     * Expected result: Each role should be present with the expected number of permissions.
     */
    @Test
    void shouldPersistRolesWithPermissions() {
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        assertThat(adminRole).isPresent();
        assertThat(adminRole.get().getPermissions()).hasSize(3); // Admin should have 2 permissions.

        Optional<Role> managerRole = roleRepository.findByName("ROLE_MANAGER");
        assertThat(managerRole).isPresent();
        assertThat(managerRole.get().getPermissions()).hasSize(2); // Manager should have 1 permission.

        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        assertThat(userRole).isPresent();
        assertThat(userRole.get().getPermissions()).hasSize(1); // Regular user should have 1 permission.
    }

    /**
     * Test: Ensures that running the DataLoader multiple times does not create duplicate entries.
     * Expected result: The number of roles, users, and permissions should remain the same.
     */
    @Test
    void shouldNotDuplicateEntitiesOnSecondRun() {
        long permissionCountBefore = permissionRepository.count();
        long roleCountBefore = roleRepository.count();
        long userCountBefore = userRepository.count();

        dataLoader.run(); // Running again to check for duplicates.

        assertThat(permissionRepository.count()).isEqualTo(permissionCountBefore);
        assertThat(roleRepository.count()).isEqualTo(roleCountBefore);
        assertThat(userRepository.count()).isEqualTo(userCountBefore);
    }
}
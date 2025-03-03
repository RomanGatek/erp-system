package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.repositories.*;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private PasswordSecurity passwordSecurity;

    @InjectMocks
    private DataLoader dataLoader;


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    DataLoaderTest(@org.springframework.beans.factory.annotation.Autowired RoleRepository roleRepository,
                   @org.springframework.beans.factory.annotation.Autowired UserRepository userRepository,
                   @org.springframework.beans.factory.annotation.Autowired PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @BeforeEach
    void setUp() {
        when(passwordSecurity.encode(anyString())).thenReturn("hashedPassword");

        dataLoader = new DataLoader(roleRepository, userRepository, permissionRepository, passwordSecurity, productRepository, inventoryRepository);
        dataLoader.run();
    }

    @Test
    void shouldPersistPermissions() {
        assertThat(permissionRepository.findByName("READ_REPORTS")).isPresent();
        assertThat(permissionRepository.findByName("APPROVE_BUDGETS")).isPresent();
        assertThat(permissionRepository.findByName("VIEW_PROFILE")).isPresent();
    }

    @Test
    void shouldPersistRolesWithPermissions() {
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        assertThat(adminRole).isPresent();
        assertThat(adminRole.get().getPermissions()).hasSize(2);

        Optional<Role> managerRole = roleRepository.findByName("ROLE_MANAGER");
        assertThat(managerRole).isPresent();
        assertThat(managerRole.get().getPermissions()).hasSize(1);

        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        assertThat(userRole).isPresent();
        assertThat(userRole.get().getPermissions()).hasSize(1);
    }

    @Test
    void shouldNotDuplicateEntitiesOnSecondRun() {
        long permissionCountBefore = permissionRepository.count();
        long roleCountBefore = roleRepository.count();
        long userCountBefore = userRepository.count();

        dataLoader.run();

        assertThat(permissionRepository.count()).isEqualTo(permissionCountBefore);
        assertThat(roleRepository.count()).isEqualTo(roleCountBefore);
        assertThat(userRepository.count()).isEqualTo(userCountBefore);
    }
}
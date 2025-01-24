package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final String DEFAULT_LAST_NAME = "User"; // Default last name
    private static final boolean DEFAULT_IS_ACTIVE = true;  // Default active status

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create permissions
        Permission readReports = new Permission("READ_REPORTS");
        Permission approveBudgets = new Permission("APPROVE_BUDGETS");
        Permission viewProfile = new Permission("VIEW_PROFILE");
        permissionRepository.saveAll(List.of(readReports, approveBudgets, viewProfile));

        // Create roles with permissions
        Role adminRole = new Role("ROLE_ADMIN", Set.of(readReports, approveBudgets));
        Role managerRole = new Role("ROLE_MANAGER", Set.of(readReports));
        Role userRole = new Role("ROLE_USER", Set.of(viewProfile));
        roleRepository.saveAll(List.of(adminRole, managerRole, userRole));

        // Create users
        User admin = createUser("admin", "Admin", "admin@example.com", Set.of(adminRole));
        User manager = createUser("manager", "Manager", "manager@example.com", Set.of(managerRole));
        User user = createUser("user", "Regular", "user@example.com", Set.of(userRole));

        userRepository.saveAll(List.of(admin, manager, user));
    }

    private User createUser(String username, String firstName, String email, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123")); // Default password
        user.setFirstName(firstName);
        user.setLastName(DEFAULT_LAST_NAME); // Default last name
        user.setEmail(email);
        user.setActive(DEFAULT_IS_ACTIVE); // Default active status
        user.setRoles(roles);
        return user;
    }
}
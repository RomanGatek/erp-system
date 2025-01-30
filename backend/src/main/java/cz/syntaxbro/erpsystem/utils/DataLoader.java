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

import java.util.Optional;
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
        Optional<Permission> readReportsFromDb = permissionRepository.findByName("READ_REPORTS");
        Permission readReports = readReportsFromDb.orElseGet(() ->
                permissionRepository.save(new Permission("READ_REPORTS"))
        );
        Optional<Permission> approveBudgetsFromDb = permissionRepository.findByName("APPROVE_BUDGETS");
        Permission approveBudgets = readReportsFromDb.orElseGet(() ->
                permissionRepository.save(new Permission("APPROVE_BUDGETS"))
        );
        Optional<Permission> viewProfileFromDb = permissionRepository.findByName("VIEW_PROFILE");
        Permission viewProfile = viewProfileFromDb.orElseGet(() ->
                permissionRepository.save(new Permission("VIEW_PROFILE"))
        );

        // Create roles with permissions
        Optional<Role> adminRoleFromDb = roleRepository.findByName("ROLE_ADMIN");
        Role adminRole = adminRoleFromDb.orElseGet(() ->
                roleRepository.save(new Role("ROLE_ADMIN", Set.of(readReports, approveBudgets)))
        );

        Optional<Role> managerRoleFromDb = roleRepository.findByName("ROLE_MANAGER");
        Role managerRole = managerRoleFromDb.orElseGet(() ->
                roleRepository.save(new Role("ROLE_MANAGER", Set.of(readReports)))
        );

        Optional<Role> userRoleFromDb = roleRepository.findByName("ROLE_USER");
        Role userRole = userRoleFromDb.orElseGet(() ->
                roleRepository.save(new Role("ROLE_USER", Set.of(viewProfile)))
        );

        // Create users
        Optional<User> adminFromDb = userRepository.findByUsername("admin");
        if (adminFromDb.isEmpty()) {
            userRepository.save(
                    createUser("admin", "Admin", "admin@example.com", Set.of(adminRole))
            );
        }

        Optional<User> managerFromDb = userRepository.findByUsername("manager");
        if (managerFromDb.isEmpty()) {
            userRepository.save(
                    createUser("manager", "Manager", "manager@example.com", Set.of(managerRole))
            );
        }

        Optional<User> userFromDb = userRepository.findByUsername("user");
        if (userFromDb.isEmpty()) {
            userRepository.save(
                    createUser("user", "Regular", "user@example.com", Set.of(userRole))
            );
        }
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
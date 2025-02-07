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

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Adding permissions
        Permission readReports = getOrCreatePermission("READ_REPORTS");
        Permission approveBudgets = getOrCreatePermission("APPROVE_BUDGETS");
        Permission viewProfile = getOrCreatePermission("VIEW_PROFILE");

        // 2. Adding roles only if they don't exist
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ROLE_ADMIN", Set.of(readReports, approveBudgets));
            Role managerRole = new Role("ROLE_MANAGER", Set.of(readReports));
            Role userRole = new Role("ROLE_USER", Set.of(viewProfile));

            roleRepository.saveAll(List.of(adminRole, managerRole, userRole));
        }

        // 3. Adding users only if they don't exist
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Role managerRole = roleRepository.findByName("ROLE_MANAGER").orElseThrow();
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();

            User admin = createUser("admin", "Admin", "admin@example.com", Set.of(adminRole));
            User manager = createUser("manager", "Manager", "manager@example.com", Set.of(managerRole));
            User user = createUser("user", "Regular", "user@example.com", Set.of(userRole));

            userRepository.saveAll(List.of(admin, manager, user));
        }
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name)));
    }

    private User createUser(String username, String firstName, String email, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName(firstName);
        user.setLastName("User");
        user.setEmail(email);
        user.setActive(true);
        user.setRoles(roles);
        return user;
    }
}
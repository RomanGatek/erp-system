package cz.syntaxbro.erpsystem.utils;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.Permission;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.PermissionRepository;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
//    private final PasswordEncoder passwordEncoder;
    private PasswordSecurity passwordSecurity;


    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      PermissionRepository permissionRepository, PasswordSecurity passwordSecurity) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordSecurity = passwordSecurity;
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
        createUserIfNotExists("admin", "Admin", "admin@example.com", Set.of(adminRole));
        createUserIfNotExists("manager", "Manager", "manager@example.com", Set.of(managerRole));
        createUserIfNotExists("user", "Regular", "user@example.com", Set.of(userRole));
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
        Optional<User> userFromDb = userRepository.findByUsername(username);
        if (userFromDb.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordSecurity.hashPassword("password123")); // Default password
            user.setFirstName(firstName);
            user.setLastName("USER"); // Default last name
            user.setEmail(email);
            user.setActive(true); // Default active status
            user.setRoles(roles);

            userRepository.save(user);
        }
    }
}
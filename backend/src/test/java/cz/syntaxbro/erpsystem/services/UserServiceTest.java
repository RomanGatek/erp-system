package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // Loads the complete Spring Boot application for testing
@Transactional // Each test runs in its own transaction
@ActiveProfiles("test") // Activates the "test" profile for this class
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordSecurity passwordSecurity;

    @Test
    @Rollback // Rolls back changes made during the test
    void shouldCreateAndRetrieveUser() {
        log.info("Starting shouldCreateAndRetrieveUser test...");

        // 1. Create a test role
        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);
        log.info("Role '{}' created and saved.", role.getName());

        // 2. Create a CreateUserRequest (instead of UserDto)
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "testuser",  // Username
                "StrongPassword1!",  // Password
                "testuser@example.com",  // Email
                "Test",  // First name
                "User",  // Last name
                true,  // Active
                Set.of("ROLE_TEST")  // Roles
        );

        log.info("Password in CreateUserRequest: {}", createUserRequest.getPassword());

        // 3. Create a user
        User createdUser = userService.createUser(createUserRequest);
        log.info("Password of the created user: {}", createdUser.getPassword());

        // 4. Verify that the user was created
        Optional<User> optionalUserFromDb = userRepository.findByUsername("testuser");

        assertThat(optionalUserFromDb).isPresent();
        User userFromDb = optionalUserFromDb.get();

        log.info("Password stored in DB: {}", userFromDb.getPassword());

        assertThat(userFromDb.getFirstName()).isEqualTo("Test");
        assertThat(userFromDb.getLastName()).isEqualTo("User");
        assertThat(userFromDb.getPassword()).isNotEmpty();

        log.info("Test shouldCreateAndRetrieveUser PASSED.");
    }

    @Test
    @Rollback
    void shouldReturnAllUsers() {
        log.info("Starting shouldReturnAllUsers test...");

        // Clears all users in the test database
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setEmail("user1@example.com");
        user1.setActive(true);
        user1.setRoles(Set.of(role));
        user1.setPassword(passwordSecurity.encode("password123"));
        userRepository.save(user1);
        log.info("User '{}' created.", user1.getUsername());

        User user2 = new User();
        user2.setUsername("user2");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setEmail("user2@example.com");
        user2.setActive(true);
        user2.setRoles(Set.of(role));
        user2.setPassword(passwordSecurity.encode("password123"));
        userRepository.save(user2);
        log.info("User '{}' created.", user2.getUsername());

        // 3. Verify that both users are retrieved
        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).containsExactlyInAnyOrder("user1", "user2");

        log.info("Test shouldReturnAllUsers PASSED.");
    }
}
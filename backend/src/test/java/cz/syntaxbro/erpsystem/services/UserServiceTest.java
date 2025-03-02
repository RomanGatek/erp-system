package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.services.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
@Transactional // Ensures each test runs in its own transaction
@ActiveProfiles("test") // Uses the "test" profile
@Slf4j
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordSecurity passwordSecurity;

    @InjectMocks
    private UserServiceImpl userService; // Uses real service with injected mocks

    @BeforeEach
    void setup() {
        log.info("Setting up mocks for UserServiceTest...");
    }

    /**
     * Tests user creation and retrieval.
     * Expected Result:
     * - The created user exists and matches the provided details.
     */
    @Test
    @Rollback
    void shouldCreateAndRetrieveUser() {
        log.info("Starting shouldCreateAndRetrieveUser test...");

        // 1. Create a test role
        Role role = new Role();
        role.setName("ROLE_TEST");  // Ensure this is correctly formatted.

        // Mock repository response for role lookup with the ACTUAL role name used in the service
        when(roleRepository.findByName("ROLE_ROLE_TEST")).thenReturn(Optional.of(role));

        // 2. Simulate a request to create a user
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "testuser",
                "StrongPassword1!",
                "testuser@example.com",
                "Test",
                "User",
                true,
                Set.of("ROLE_TEST") // Make sure the role name matches expected format.
        );

        // 3. Simulate password encryption
        when(passwordSecurity.encode(anyString())).thenReturn("encodedPassword");

        // 4. Simulate saving the user in the database
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("testuser@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setActive(true);
        mockUser.setPassword("encodedPassword");
        mockUser.setRoles(Set.of(role));

        // **Remove unnecessary stubbing**
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // 5. Call the method that creates a user
        User createdUser = userService.createUser(createUserRequest);

        // 6. Verify that the user was created successfully
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");

        log.info("Test shouldCreateAndRetrieveUser PASSED.");

        // Capture the role name used in the actual method call
        ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
        verify(roleRepository).findByName(roleCaptor.capture());

        // Debug: Print out the actual role name used
        log.info("Actual Role Name Used: {}", roleCaptor.getValue());

        // Ensure the role name matches what is expected
        assertThat(roleCaptor.getValue()).isEqualTo("ROLE_ROLE_TEST"); // Ensure correct value!
    }

    /**
     * Tests retrieving all users in the system.
     * Expected Result:
     * - The method returns a list containing exactly two users.
     */
    @Test
    @Rollback
    void shouldReturnAllUsers() {
        log.info("Starting shouldReturnAllUsers test...");

        // 1. Create a test role
        Role role = new Role();
        role.setName("ROLE_TEST");

        // 2. Create two test users
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRoles(Set.of(role));
        user1.setPassword("password123");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRoles(Set.of(role));
        user2.setPassword("password123");

        // 3. Simulate repository behavior for `findAll`
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // 4. Call the method that returns all users
        List<User> users = userService.getAllUsers();

        // 5. Verify that the method returned the correct number of users
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).containsExactlyInAnyOrder("user1", "user2");

        log.info("Test shouldReturnAllUsers PASSED.");
    }

    /**
     * Tests if the `getUserById()` method throws an exception when the user does not exist.
     * Expected Result:
     * - The method throws a `RuntimeException`.
     */
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        log.info("Starting shouldThrowExceptionWhenUserNotFound test...");

        // 1. Simulate that the user with ID 1 does not exist
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 2. Verify that the method throws the correct exception
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));

        log.info("Test shouldThrowExceptionWhenUserNotFound PASSED.");
    }
}
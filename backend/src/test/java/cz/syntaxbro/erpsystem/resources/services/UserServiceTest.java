package cz.syntaxbro.erpsystem.resources.services;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // Loads the complete Spring Boot application for testing
@Transactional // Each test runs in its own transaction
@ActiveProfiles("test") // Activates the "test" profile for this class
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Rollback // Rolls back changes made during the test
    void shouldCreateAndRetrieveUser() {
        // 1. Create a test role
        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        // 2. Create a user
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setEmail("testuser@example.com");
        userDto.setActive(true);
        userDto.setRoles(Set.of("ROLE_TEST"));

        UserDto createdUser = userService.createUser(userDto);

        // 3. Verify that the user was created
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");

        // 4. Retrieve the user from the database
        User userFromDb = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(userFromDb.getFirstName()).isEqualTo("Test");
        assertThat(userFromDb.getLastName()).isEqualTo("User");
    }

    @Test
    @Rollback
    void shouldReturnAllUsers() {
        // 1. Create test users
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
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setEmail("user2@example.com");
        user2.setActive(true);
        user2.setRoles(Set.of(role));
        userRepository.save(user2);

        // 2. Verify that both users are retrieved
        List<UserDto> users = userService.getAllUsers();
        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getUsername).containsExactlyInAnyOrder("user1", "user2");
    }
}
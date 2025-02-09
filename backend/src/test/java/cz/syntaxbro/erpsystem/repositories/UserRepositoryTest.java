package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Test: Saves a user and finds it by username.
     */
    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("securePassword123");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
    }

    /**
     * Test: Searching for a non-existent user by username.
     */
    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> foundUser = userRepository.findByUsername("nonExistentUser");

        assertThat(foundUser).isEmpty();
    }

    /**
     * Test: Verifies that a user exists by username.
     */
    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("user@example.com");
        user.setPassword("strongPassword123");
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("existingUser");

        assertThat(exists).isTrue();
    }

    /**
     * Test: Verify that there is no user with username.
     */
    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        boolean exists = userRepository.existsByUsername("nonExistingUser");

        assertThat(exists).isFalse();
    }

    /**
     * Test: Verify that a user exists by email.
     */
    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        User user = new User();
        user.setUsername("emailUser");
        user.setEmail("email@example.com");
        user.setPassword("securePassword123");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("email@example.com");

        assertThat(exists).isTrue();
    }

    /**
     * Test: Verify that the user by email does not exist.
     */
    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonExisting@example.com");

        assertThat(exists).isFalse();
    }
}
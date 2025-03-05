package cz.syntaxbro.erpsystem.security.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("securePassword123");
        testUser.setActive(true);
    }

    /**
     * Tests that loadUserByUsername() correctly retrieves a user from the repository.
     * Expected output:
     * - The method should return a valid UserDetails object.
     * - The email in the UserDetails should match the email of the test user.
     */
    @Test
    void testLoadUserByUsername_Success() {
        // Mock repository to return testUser when findByEmail is called
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Call service method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(testUser.getEmail());

        // Verify returned user details
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(testUser.getEmail(), userDetails.getUsername(), "Email should match username in UserDetails");
    }

    /**
     * Tests that loadUserByUsername() throws UsernameNotFoundException when the user is not found.
     * Expected output:
     * - The method should throw UsernameNotFoundException.
     */
    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock repository to return empty Optional when user is not found
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Expect UsernameNotFoundException when calling the method
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com"),
                "Expected UsernameNotFoundException for unknown user"
        );
    }
}
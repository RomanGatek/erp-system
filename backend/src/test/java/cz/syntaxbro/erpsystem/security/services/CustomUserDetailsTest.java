package cz.syntaxbro.erpsystem.security.services;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private User testUser;
    private CustomUserDetails customUserDetails;

    /**
     * Sets up a test user before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("securePassword123");
        testUser.setActive(true);
        testUser.setRoles(Set.of(new Role("ROLE_USER")));

        customUserDetails = new CustomUserDetails(testUser);
    }

    /**
     * Tests that the getAuthorities() method correctly returns the user's roles.
     * Expected output:
     * - The method should return a set containing "ROLE_USER".
     */
    @Test
    void testGetAuthorities() {
        Set<String> expectedRoles = Set.of("ROLE_USER");

        Set<String> actualRoles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        assertEquals(expectedRoles, actualRoles, "User authorities do not match expected roles");
    }

    /**
     * Tests that the getPassword() method returns the correct password.
     * Expected output:
     * - The method should return the password set in the test user.
     */
    @Test
    void testGetPassword() {
        assertEquals(testUser.getPassword(), customUserDetails.getPassword(), "Password does not match");
    }

    /**
     * Tests that the getUsername() method returns the correct email.
     * Expected output:
     * - The method should return the email set in the test user.
     */
    @Test
    void testGetUsername() {
        assertEquals(testUser.getEmail(), customUserDetails.getUsername(), "Username (email) does not match");
    }

    /**
     * Tests that the isEnabled() method correctly returns whether the user is active.
     * Expected output:
     * - The method should return `true` since the test user is active.
     */
    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled(), "User should be enabled");
    }

    /**
     * Tests that the unimplemented methods return `true` by default.
     * Expected output:
     * - `isAccountNonExpired()`, `isAccountNonLocked()`, and `isCredentialsNonExpired()` should all return `true`.
     */
    @Test
    void testNonImplementedMethodsReturnTrue() {
        assertTrue(customUserDetails.isAccountNonExpired(), "Account should be non-expired");
        assertTrue(customUserDetails.isAccountNonLocked(), "Account should be non-locked");
        assertTrue(customUserDetails.isCredentialsNonExpired(), "Credentials should be non-expired");
    }
}
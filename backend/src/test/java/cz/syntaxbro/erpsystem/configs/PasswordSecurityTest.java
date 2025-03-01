package cz.syntaxbro.erpsystem.configs;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PasswordSecurityTest {

    private final PasswordSecurity passwordSecurity = new PasswordSecurity();
    private User user;

    @BeforeEach
    void setUp() {
        Set<Role> roles = Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER")
                .stream().map(Role::new).collect(Collectors.toUnmodifiableSet());

        this.user = User
                .builder()
                .id((long) 1)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .password(passwordSecurity.encode("Password123@"))
                .email("test@test.test")
                .active(true)
                .roles(roles)
                .build();
    }

    @Test
    void hashPassword() {
        assertFalse(passwordSecurity.matches("Password123", this.user.getPassword()));
    }

    @Test
    void passwordValidateSuccessfully() {
        this.user.setPassword("1T!password");
        boolean isValidated = passwordSecurity.passwordValidator(this.user.getPassword());
        assertTrue(isValidated);
    }
}
package cz.syntaxbro.erpsystem.configs;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordSecurityTest {

    private final PasswordSecurity passwordSecurity = new PasswordSecurity();
    private User user;
    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .id(1L)
                .password("!A1password")
                .roles(Set.of(new Role("ROLE_ADMIN")))
                .username("admin")
                .email("admin@admin.net")
                .isActive(true)
                .build();

    }

    @Test
    void hashPassword() {
        //Act
        boolean isValidated = "password".equals(this.user.getPassword());
        //Assert
        assertFalse(isValidated);

    }

    @Test
    void passwordValidateFailed() {
        this.user.setPassword("password");
        //Act
        boolean isValidated = passwordSecurity.passwordValidator(this.user.getPassword());
        //Assert
        assertFalse(isValidated);
    }
    @Test
    void passwordValidateSuccessfully() {
        //Arrange
        //Act
        boolean isValidated = passwordSecurity.passwordValidator(this.user.getPassword());
        //Assert
        assertTrue(isValidated);
    }
}
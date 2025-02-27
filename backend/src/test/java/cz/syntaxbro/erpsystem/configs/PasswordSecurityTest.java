package cz.syntaxbro.erpsystem.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordSecurityTest {

    private final PasswordSecurity passwordSecurity = new PasswordSecurity();
    private UserDto userDto = new UserDto();

    @BeforeEach
    void setUp() {
        this.userDto = new UserDto(
                1L,              // User ID
                "testUser",      // Username
                "Test",          // First name
                "User",          // Last name
                passwordSecurity.encode("password"), //Password
                "test@example.com", // Email
                true,            // Active status
                Set.of("ROLE_USER") // Assigned roles
        );
    }

    @Test
    void hashPassword() {
        //Act
        boolean isValidated = "password".equals(this.userDto.getPassword());
        //Assert
        assertFalse(isValidated);

    }

    @Test
    void passwordValidateFailed() {
        //Act
        boolean isValidated = passwordSecurity.passwordValidator(this.userDto.getPassword());
        //Assert
        assertFalse(isValidated);
    }
    @Test
    void passwordValidateSuccessfully() {
        //Arrange
        this.userDto.setPassword("1T!password");
        //Act
        boolean isValidated = passwordSecurity.passwordValidator(this.userDto.getPassword());
        //Assert
        assertTrue(isValidated);
    }
}
package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest {

    AuthServiceImpl authServiceImpl;
    AutoCloseable autoCloseable;
    SignUpRequest signUpRequest;

    @Mock
    UserService userService;

    @Mock
    PasswordSecurity passwordSecurity;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authServiceImpl = new AuthServiceImpl(userService, passwordSecurity);
        this.signUpRequest = new SignUpRequest("Username", "1!Password", "email@email.com");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerUserTestUsernameIsEmpty() {
        //Arrest
        this.signUpRequest.setUsername("");
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authServiceImpl.registerUser(signUpRequest));
        //Asser
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    @Test
    void registerUserTestEmailIsEmpty() {
        //Arrest
        this.signUpRequest.setEmail("");
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authServiceImpl.registerUser(signUpRequest));
        //Asser
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void registerUserTestPasswordIsEmpty() {
        //Arrest
        this.signUpRequest.setPassword("");
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authServiceImpl.registerUser(signUpRequest));
        //Asser
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    @Test
    void registerUserTestPasswordEmpty() {

    }

    @Test
    void authenticateUser() {
    }
}
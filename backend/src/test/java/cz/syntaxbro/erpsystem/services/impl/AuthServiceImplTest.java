package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    void authenticateUserUsernameException() {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("Username", "1!Password");
        when(userService.getUserByUsername(loginRequest.getUsername())).thenReturn(null);
        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.authenticateUser(loginRequest));
        //Assert
        assertEquals("401 UNAUTHORIZED \"Invalid username\"", exception.getMessage());
    }

    @Test
    void authenticateUserPasswordException() {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("username", "1!Password");
        User user = new User();
        user.setUsername("username");
        user.setPassword("deferment secure Password");
        when(userService.getUserByUsername(loginRequest.getUsername())).thenReturn(user);
        when(passwordSecurity.hashPassword(loginRequest.getPassword())).thenReturn("wrong password");
        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.authenticateUser(loginRequest));
        //Assert
        assertEquals("401 UNAUTHORIZED \"Invalid password\"", exception.getMessage());
    }

    @Test
    void authenticateUserSuccess() {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("username", "1!Password");
        User user = new User();
        user.setUsername("username");
        user.setPassword("1!Password");
        when(userService.getUserByUsername(loginRequest.getUsername())).thenReturn(user);
        when(passwordSecurity.hashPassword(loginRequest.getPassword())).thenReturn("1!Password");
        //Act
        String response = authServiceImpl.authenticateUser(loginRequest);
        //Assert
        assertEquals("generated-jwt-token", response);
    }
}
package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthServiceImpl authServiceImpl;
    private AutoCloseable autoCloseable;

    @Mock
    private UserService userService;

    @Mock
    private PasswordSecurity passwordSecurity;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authServiceImpl = new AuthServiceImpl(userService, passwordSecurity);
        signUpRequest = new SignUpRequest("Username", "1!Password", "email@email.com");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerUser_shouldThrowException_whenUsernameExists() {
        when(userService.getUserByUsername("Username")).thenReturn(new User());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authServiceImpl.registerUser(signUpRequest));

        assertEquals("Username already exists: Username", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldThrowException_whenInvalidUsername() {
        when(userService.getUserByUsername("Username")).thenReturn(null);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> authServiceImpl.authenticateUser(new LoginRequest("Username", "1!Password")));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldThrowException_whenInvalidPassword() {
        User user = new User();
        user.setPassword("wrongPassword");
        when(userService.getUserByUsername("Username")).thenReturn(user);
        when(passwordSecurity.matches("1!Password", "wrongPassword")).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> authServiceImpl.authenticateUser(new LoginRequest("Username", "1!Password")));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldReturnToken_whenValidCredentials() {
        User user = new User();
        user.setPassword("hashedPassword");
        when(userService.getUserByUsername("Username")).thenReturn(user);
        when(passwordSecurity.matches("1!Password", "hashedPassword")).thenReturn(true);

        String token = authServiceImpl.authenticateUser(new LoginRequest("Username", "1!Password"));

        assertEquals("generated-jwt-token", token);
    }
}
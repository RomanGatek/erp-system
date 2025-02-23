package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.utils.JwtUtil;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthServiceImpl authServiceImpl;
    private AutoCloseable autoCloseable;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordSecurity passwordSecurity;

    @Mock
    private JwtUtil jwtUtil;

    private SignUpRequest signUpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authServiceImpl = new AuthServiceImpl(userRepository, jwtUtil, passwordSecurity);

        signUpRequest = new SignUpRequest("Username", "1!Password", "email@email.com");

        user = new User();
        user.setEmail("email@email.com");
        user.setPassword("hashedPassword");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerUser_shouldNotThrowException_whenUsernameExists() {
        when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authServiceImpl.registerUser(signUpRequest));

        verify(userRepository, never()).findByEmail(signUpRequest.getEmail());
    }


    @Test
    void authenticateUser_shouldThrowException_whenInvalidUsername() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password")));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldThrowException_whenInvalidPassword() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("1!Password", "hashedPassword")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password")));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldReturnToken_whenValidCredentials() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("1!Password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("generated-jwt-token");

        String token = authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password"));

        assertEquals("generated-jwt-token", token);
    }
}
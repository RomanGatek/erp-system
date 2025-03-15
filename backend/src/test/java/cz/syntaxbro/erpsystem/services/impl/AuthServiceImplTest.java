package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.exceptions.GlobalExceptionHandler;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.utils.JwtUtil;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class AuthServiceImplTest {

    private AutoCloseable autoCloseable;
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordSecurity passwordSecurity;

    @Mock
    CustomUserDetails customUserDetails;

    @Mock
    SecurityContext securityContext;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Spy
    @InjectMocks
    private AuthServiceImpl authService;

    private SignUpRequest signUpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authServiceImpl = new AuthServiceImpl(userRepository, jwtUtil, passwordSecurity, userService);

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
    void testRegisterUser_WhenUsernameEqualsNull(){
        //Arrest
        SignUpRequest signUpRequest = new SignUpRequest("", "1!Password", "email@email.com");
        //Act
        IllegalArgumentException  exceptionResult = assertThrows(IllegalArgumentException .class, () ->
                authServiceImpl.registerUser(signUpRequest));
        //Assert
        assertEquals("Username" + " cannot be null or empty", exceptionResult.getMessage());
    }

    @Test
    void testRegisterUser_WhenEmailEqualsNull(){
        //Arrest
        SignUpRequest signUpRequest = new SignUpRequest("username", "1!Password", "");
        //Act
        IllegalArgumentException  exceptionResult = assertThrows(IllegalArgumentException .class, () ->
                authServiceImpl.registerUser(signUpRequest));
        //Assert
        assertEquals("Email" + " cannot be null or empty", exceptionResult.getMessage());
    }

    @Test
    void testRegisterUser_WhenPasswordEqualsNull(){
        //Arrest
        SignUpRequest signUpRequest = new SignUpRequest("username", "", "email@email.com");
        //Act
        IllegalArgumentException  exceptionResult = assertThrows(IllegalArgumentException .class, () ->
                authServiceImpl.registerUser(signUpRequest));
        //Assert
        assertEquals("Password" + " cannot be null or empty", exceptionResult.getMessage());
    }

    @Test
    void authenticateUser_shouldThrowException_whenInvalidUsername() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.empty());

        GlobalExceptionHandler.UserNotFoundException exception = assertThrows(GlobalExceptionHandler.UserNotFoundException.class,
                () -> authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password")));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldThrowException_whenInvalidPassword() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("1!Password", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password")));

        assertEquals("[password];Invalid email or password", exception.getMessage());
    }

    @Test
    void authenticateUser_shouldReturnToken_whenValidCredentials() {
        when(userRepository.findByEmail("email@email.com")).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("1!Password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("generated-jwt-token");

        String token = authServiceImpl.authenticateUser(new LoginRequest("email@email.com", "1!Password"));

        assertEquals("generated-jwt-token", token);
    }

    @Test
    void testGetCurrentUser_NoAuthenticatedUser(){
        //Arrest
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        when(customUserDetails.getUsername()).thenReturn(null);
        //Act
        IllegalStateException exceptionResult = assertThrows(IllegalStateException.class, () ->
                authService.getCurrentUser());
        //Assert
        assertEquals("No authenticated user found" , exceptionResult.getMessage());
        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    void testGetCurrentUser_AuthenticatedUser(){
        //Arrest
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(customUserDetails.getUsername()).thenReturn(this.user.getEmail());
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(userRepository.findByEmail(this.user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getUserByEmail(this.user.getEmail())).thenReturn(this.user);
        //Act
        User result = authService.getCurrentUser();
        //Assert
        assertEquals(this.user , result);
        verify(authService, times(1)).getCurrentUser();
    }
}

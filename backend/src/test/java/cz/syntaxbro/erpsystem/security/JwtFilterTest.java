package cz.syntaxbro.erpsystem.security;

import cz.syntaxbro.erpsystem.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for JwtFilter.
 * Ensures that JWT tokens are correctly processed and authentication is applied.
 */
@SpringBootTest(properties = {"spring.profiles.active=test"})
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private final String validToken = "valid.jwt.token";
    private UserDetails userDetails;

    /**
     * Setup mocks and initialize test dependencies.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);

        // Always clear SecurityContext before each test
        SecurityContextHolder.clearContext();

        String username = "testUser";
        userDetails = new User(username, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    }

    /**
     * Test: Valid JWT token should authenticate the user.
     * Expected: Security context should be set with correct authentication.
     */
    @Test
    void shouldAuthenticateUser_whenValidTokenProvided() throws ServletException, IOException {
        // Act: Execute the filter
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Verify authentication is set in security context
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(userDetails);

        // Verify that request was passed to next filter
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test: Invalid JWT token should NOT authenticate the user.
     * Expected: Security context should remain empty.
     */
    @Test
    void shouldNotAuthenticateUser_whenInvalidTokenProvided() throws ServletException, IOException {
        // Arrange: Mock invalid token scenario
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Ensure extractUsername() returns a valid username
        when(jwtUtil.extractUsername(validToken)).thenReturn("testUser");

        // Ensure userDetailsService.loadUserByUsername() is called normally
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

        // Ensure validateToken() returns false (invalid token)
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(false);

        // Act: Execute the filter
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Authentication should NOT be set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Ensure validateToken was called with correct parameters
        verify(jwtUtil).validateToken(validToken, userDetails);

        // Ensure userDetailsService.loadUserByUsername() was called
        verify(userDetailsService).loadUserByUsername("testUser");

        // Ensure request proceeds
        verify(filterChain).doFilter(request, response);
    }


    /**
     * Test: Missing Authorization header should NOT authenticate user.
     * Expected: Security context should remain empty.
     */
    @Test
    void shouldNotAuthenticateUser_whenNoAuthorizationHeader() throws ServletException, IOException {
        // Arrange: No Authorization header present
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act: Execute the filter
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Security context should remain empty
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Ensure extractUsername was **never called**
        verify(jwtUtil, never()).extractUsername(anyString());

        // Ensure validateToken was **never called**
        verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));

        // Ensure loadUserByUsername was **never called**
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        // Ensure request proceeds
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test: Authorization header without "Bearer" should NOT authenticate the user.
     * Expected: Security context should remain empty.
     */
    @Test
    void shouldNotAuthenticateUser_whenHeaderWithoutBearerPrefix() throws ServletException, IOException {
        // Arrange: Mock header without "Bearer" prefix
        when(request.getHeader("Authorization")).thenReturn("InvalidTokenFormat");

        // Act: Execute the filter
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert: Security context should remain empty
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify request was passed to next filter
        verify(filterChain).doFilter(request, response);
    }
}
package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;

    @TestConfiguration
    static class AuthServiceTestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest("testUser", "Password123@", "test@example.com");
        loginRequest = new LoginRequest("testUser", "Password123@");
        user = User.builder()
                .id(1L)
                .username("testUser")
                .password("Password123@")
                .firstName("testUserFirstName")
                .lastName("testUserLastName")
                .email("test@test.test")
                .active(true)
                .roles(Set.of(new Role("ROLE_USER")))
                .build();
    }

    /**
     * Test: User Registration
     * Expected Result: 201 Created
     */
    @Test
    void signup_shouldReturnCreated_whenValidRequest() throws Exception {
        doNothing().when(authService).registerUser(any(SignUpRequest.class));

        mockMvc.perform(post("/api/auth/public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).registerUser(any(SignUpRequest.class));
    }

    /**
     * Test: Login Successful
     * Expected Result: 200 OK + token
     */
    @Test
    void login_shouldReturnOk_whenValidCredentials() throws Exception {
        String mockToken = "mocked-jwt-token";
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"accessToken\":\"%s\",\"refreshToken\":\"%s\"}", mockToken, mockToken)));

        verify(authService, times(1)).authenticateUser(any(LoginRequest.class));
    }

    /**
     * Test: Logged-in user gets their data
     * Expected result: 200 OK + JSON with user data
     */
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void getCurrentUser_shouldReturnUser_whenAuthenticated() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(authService, times(1)).getCurrentUser();
    }

    /**
     * Test: Unauthorized user cannot retrieve their data
     * Expected result: 401 Unauthorized
     */
    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().is(401));
    }
}
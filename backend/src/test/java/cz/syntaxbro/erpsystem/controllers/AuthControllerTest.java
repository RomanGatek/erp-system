package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
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
@SpringBootTest
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
    private UserDto userDto;

    @TestConfiguration
    static class AuthServiceTestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest("testUser", "password", "test@example.com");
        loginRequest = new LoginRequest("testUser", "password");
        userDto = new UserDto(1L, "testUser", "Test", "User","pass", "test@example.com", true, Set.of("ROLE_USER"));
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
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

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
                .andExpect(content().string("Login successfull! Token: " + mockToken));

        verify(authService, times(1)).authenticateUser(any(LoginRequest.class));
    }

    /**
     * Test: Logged in user gets their data
     * Expected result: 200 OK + JSON with user data
     */
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void getCurrentUser_shouldReturnUser_whenAuthenticated() throws Exception {
        when(authService.getCurrentUser()).thenReturn(userDto);

        mockMvc.perform(get("/api/auth/user/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(authService, times(1)).getCurrentUser();
    }

    /**
     * Test: Unauthorized user cannot retrieve their data
     * Expected result: 401 Unauthorized
     */
    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/user/me"))
                .andExpect(status().isUnauthorized());
    }
}
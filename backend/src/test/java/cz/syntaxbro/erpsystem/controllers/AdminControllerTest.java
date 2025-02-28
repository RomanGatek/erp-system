package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test case: Access granted for ADMIN users.
     * Expected result: HTTP 200 OK with message "Welcome, ADMIN!".
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAccess_shouldReturnOk_whenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome, ADMIN!"));
    }

    /**
     * Test case: Access denied for regular USERS.
     * Expected result: HTTP 403 Forbidden.
     */
    @Test
    @WithMockUser(roles = "USER")
    void adminAccess_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test case: Access denied for unauthenticated users (not logged in).
     * Expected result: HTTP 401 Unauthorized.
     */
    @Test
    void adminAccess_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }
}
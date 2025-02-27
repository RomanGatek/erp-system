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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ManagerController.class)
@Import(SecurityConfig.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test case: Managers can access the /api/manager endpoint.
     * Expected result: HTTP 200 OK with message "Welcome, MANAGER!".
     */
    @Test
    @WithMockUser(roles = "MANAGER")
    void managerAccess_shouldReturnOk_whenUserIsManager() throws Exception {
        mockMvc.perform(get("/api/manager"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome, MANAGER!"));
    }

    /**
     * Test case: Non-manager users (e.g., USER) cannot access /api/manager.
     * Expected result: HTTP 403 Forbidden.
     */
    @Test
    @WithMockUser(roles = "USER")
    void managerAccess_shouldReturnForbidden_whenUserIsNotManager() throws Exception {
        mockMvc.perform(get("/api/manager"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test case: Unauthorized (not logged in) users cannot access /api/manager.
     * Expected result: HTTP 401 Unauthorized.
     */
    @Test
    void managerAccess_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/manager"))
                .andExpect(status().isUnauthorized());
    }
}
package cz.syntaxbro.erpsystem.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test case: ADMIN user can access reports.
     * Expected result: HTTP 200 OK.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReports_shouldReturnOk_whenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("All reports found"));
    }

    /**
     * Test case: MANAGER user can access reports.
     * Expected result: HTTP 200 OK.
     */
    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllReports_shouldReturnOk_whenUserIsManager() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("All reports found"));
    }

    /**
     * Test case: USER should not be able to access reports.
     * Expected result: HTTP 403 Forbidden.
     */
    @Test
    @WithMockUser(roles = "USER")
    void getAllReports_shouldReturnForbidden_whenUserIsNotAdminOrManager() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Test case: Unauthenticated users should not access reports.
     * Expected result: HTTP 401 Unauthorized (or 403 Forbidden depending on SecurityConfig).
     */
    @Test
    void getAllReports_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
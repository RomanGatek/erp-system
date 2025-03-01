package cz.syntaxbro.erpsystem.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests if the public authentication endpoint is available.
     * Expects to return 404 (Not Found) if it doesn't exist.
     */
    @Test
    void shouldAllowAccessToPublicAuthEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/public/test"))
                .andExpect(status().isNotFound()); // Verifies that the endpoint does not exist or is misconfigured
    }

    /**
     * Tests whether the protected endpoint is properly secured.
     * Access without authentication should be denied (403 Forbidden).
     */
    @Test
    void shouldRequireAuthenticationForApiEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected"))
                .andExpect(status().isForbidden()); // Verifies that access is denied for an unauthenticated user
    }

    /**
     * Tests whether an authenticated user with the ADMIN role has access to the protected endpoint.
     * Expects a 200 (OK) response.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports"))
                .andExpect(status().isOk()); // Verifies that the authenticated user ADMIN has access
    }
}

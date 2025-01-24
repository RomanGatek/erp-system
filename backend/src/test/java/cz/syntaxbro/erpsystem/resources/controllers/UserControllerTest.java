package cz.syntaxbro.erpsystem.resources.controllers;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Loads the full Spring Boot application
@AutoConfigureMockMvc
@Transactional // Runs tests in a transaction
@ActiveProfiles("test") // Activates the "test" profile for this class
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldReturnUsersFromController() throws Exception {
        // Prepare test data
        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testuser@example.com");
        user.setActive(true);
        user.setRoles(Set.of(role));
        userRepository.save(user);

        // Verify the endpoint
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"));
    }
}
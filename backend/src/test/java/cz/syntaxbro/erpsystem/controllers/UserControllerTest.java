package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners(
        listeners = {
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextBeforeModesTestExecutionListener.class
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // Ensure ROLE_USER exists in the database
        roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            Role savedRole = roleRepository.save(newRole);
            roleRepository.flush();
            return savedRole;
        });

        // Ensure the role is actually found
        assertTrue(roleRepository.findByName("ROLE_USER").isPresent(), "ROLE_USER was not correctly created!");

        // Create a test user
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "testUser", "StrongPassword1!", "test@example.com",
                "Test", "User", true, Set.of("USER")
        );

        User user = userService.createUser(createUserRequest);
        testUserId = user.getId();

        assertNotNull(testUserId, "Test user was not correctly created!");
    }

    /**
     * Test: Admin can retrieve all users.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnUsersListForAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$[*].username").value(org.hamcrest.Matchers.hasItem("testUser")));
    }

    /**
     * Test: Manager can retrieve all users.
     */
    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldReturnUsersListForManager() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk());
    }

    /**
     * Test: Regular user is forbidden from retrieving all users.
     */
    @Test
    @WithMockUser
    void shouldDenyUsersListForRegularUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: Unauthenticated user is forbidden from retrieving all users.
     */
    @Test
    void shouldDenyUsersListForUnauthenticatedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test: Admin can retrieve user details by ID.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnUserDetailsForAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    /**
     * Test: Regular user is forbidden from retrieving another user's details.
     */
    @Test
    @WithMockUser
    void shouldDenyUserDetailsForRegularUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + testUserId))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: Admin can create a new user.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateUserForAdmin() throws Exception {
        String newUserJson = """
            {
                "username": "newUser",
                "password": "StrongPassword1!",
                "email": "newuser@example.com",
                "firstName": "New",
                "lastName": "User",
                "active": true,
                "roles": ["USER"]
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    /**
     * Test: Admin can update an existing user.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateUserForAdmin() throws Exception {
        String updatedUserJson = """
            {
                "username": "updatedUser",
                "email": "updated@example.com",
                "firstName": "Updated",
                "lastName": "User",
                "active": false,
                "roles": ["USER"]
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    /**
     * Test: Admin can delete a user.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteUserForAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + testUserId))
                .andExpect(status().isNoContent());

        Optional<User> deletedUser = userRepository.findById(testUserId);
        assertTrue(deletedUser.isEmpty(), "User was not deleted correctly!");
    }

    /**
     * Test: Ensure role persistence.
     */
    @Test
    void shouldSaveRoleCorrectly() {
        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByName("ROLE_TEST");
        assertTrue(foundRole.isPresent(), "ROLE_TEST was not found in the database!");
    }
}
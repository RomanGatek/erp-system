package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.security.SecurityConfig;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private CreateUserRequest createUserRequest;

    /**
     * Setup method that runs before each test case.
     * Initializes a test user with predefined values.
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("tetUser")
                .firstName("Test")
                .lastName("User")
                .password("Password123@")
                .email("test@example.com")
                .isActive(true)
                .roles(Set.of(new Role("USER")))
                .build();

        createUserRequest = new CreateUserRequest(
                "testUser", "StrongPassword1!", "test@example.com",
                "Test", "User", true, Set.of("ROLE_USER")
        );

    }

    /**
     * Test case: Retrieve a list of users.
     * Expected result: HTTP 200 OK and a non-empty list of users.
     */
    @Test
    void getUsers_shouldReturnListOfUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        ResponseEntity<List<User>> response = userController.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .allSatisfy(user -> {
                    assertThat(user.getUsername()).isNotEmpty();
                    assertThat(user.getEmail()).contains("@");
                });

        verify(userService, times(1)).getAllUsers();
    }

    /**
     * Test case: Retrieve an empty list when no users exist.
     * Expected result: HTTP 200 OK and an empty list.
     */
    @Test
    void getUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isEmpty();

        verify(userService, times(1)).getAllUsers();
    }

    /**
     * Test case: Retrieve a user by ID.
     * Expected result: HTTP 200 OK and the correct UserDto.
     */
    @Test
    void getUserById_shouldReturnUserDto_whenUserExists() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Optional<User>> response = userController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get().getUsername()).isEqualTo("testUser");

        verify(userService, times(1)).getUserById(1L);
    }

    /**
     * Test case: Attempting to retrieve a non-existent user.
     * Expected result: HTTP 404 Not Found (RuntimeException thrown).
     */
    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() {
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> userController.getUserById(1L));

        verify(userService, times(1)).getUserById(1L);
    }

    /**
     * Test case: Successfully creating a new user.
     * Expected result: HTTP 201 Created.
     */
    @Test
    void createUser_shouldReturnCreatedUser() {
        when(userService.createUser(createUserRequest)).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo(createUserRequest.getUsername());

        verify(userService, times(1)).createUser(createUserRequest);
    }

    /**
     * Test case: Successfully updating a user.
     * Expected result: HTTP 200 OK and updated user.
     */
    @Test
    void updateUser_shouldReturnUpdatedUser() {
        when(userService.updateUser(1L, createUserRequest)).thenReturn(testUser);

        ResponseEntity<User> response = userController.updateUser(1L, createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo(testUser.getUsername());

        verify(userService, times(1)).updateUser(1L, createUserRequest);
    }

    /**
     * Test case: Successfully deleting a user.
     * Expected result: HTTP 204 No Content.
     */
    @Test
    void deleteUser_shouldReturnNoContent() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService, times(1)).deleteUser(1L);
    }
}
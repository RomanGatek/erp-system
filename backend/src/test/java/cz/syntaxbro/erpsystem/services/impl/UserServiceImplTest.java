package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;
    private AutoCloseable autoCloseable;
    private User user;
    private UserDto userDto;
    private CreateUserRequest createUserRequest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordSecurity passwordSecurity;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceImpl = new UserServiceImpl(userRepository, roleRepository, passwordSecurity);

        user = new User(1L, "username", "1!Password", "firstName", "lastName", "email@email.com", true, Set.of());
        userDto = new UserDto(1L, "username", "firstName", "lastName", "1!Password", "email@email.com", true, Set.of());
        createUserRequest = new CreateUserRequest("username", "1!Password", "email@email.com", "firstName", "lastName", true, Set.of("ROLE_USER"));
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        List<User> mockUsers = List.of(user);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserDto> users = userServiceImpl.getAllUsers();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

/*    @Test
    void getUserByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserByUsername("username");

        assertNotNull(result);
        assertEquals("username", result.getUsername());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    void getUserByUsername_shouldReturnNull_whenNotFound() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        User result = userServiceImpl.getUserByUsername("username");

        assertNull(result);
        verify(userRepository, times(1)).findByUsername("username");
    }*/

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userServiceImpl.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

//    @Test
//    void createUser_shouldThrowException_whenPasswordInvalid() {
//        createUserRequest.setPassword("weakpassword");
//
//        when(passwordSecurity.passwordValidator("weakpassword")).thenReturn(false);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceImpl.createUser(createUserRequest));
//
//        assertEquals("Password must contain at least one uppercase letter, one digit, one special character, min 10 char and max 32 char", exception.getMessage());
//    }

    @Test
    void createUser_shouldHashPasswordAndSaveUser() {
        Role roleUser = new Role("ROLE_USER");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordSecurity.passwordValidator(anyString())).thenReturn(true);
        when(passwordSecurity.hashPassword(anyString())).thenReturn("$2a$10$hashedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        UserDto result = userServiceImpl.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals("$2a$10$hashedPassword", result.getPassword());
        assertNotNull(result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateAndReturnUpdatedUser() {
        Role roleUser = new Role("ROLE_USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updatedUser = userServiceImpl.updateUser(1L, createUserRequest);

        assertNotNull(updatedUser);
        assertEquals(userDto.getUsername(), updatedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userServiceImpl.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceImpl.deleteUser(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
    }
}
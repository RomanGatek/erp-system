package cz.syntaxbro.erpsystem.services.impl;


import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceImplTest {

    UserServiceImpl userServiceImpl;
    User user;
    UserDto userDto;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordSecurity passwordSecurity;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceImpl = new UserServiceImpl(userRepository, roleRepository);
        this.user = new User(1L,"username", "1!Password", "firstName", "lastName", "email@email", true, Set.of());
        this.userDto = new UserDto(
                this.user.getId(),
                this.user.getUsername(),
                this.user.getFirstName(),
                this.user.getLastName(),
                this.user.getPassword(),
                this.user.getEmail(),
                this.user.isActive(),
                Set.of());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllUsers() {
        //Arrange
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(this.user);
        when(userRepository.findAll()).thenReturn(mockUsers);
        //Act
        List<UserDto> users = userServiceImpl.getAllUsers();
        //Assert
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByUsernameFound() {
        //Arrange
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(this.user));
        //Act
        User result = userServiceImpl.getUserByUsername("username");
        //Assert
        assertNotNull(result);
        assertEquals("username", result.getUsername());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    void getUserByUsernameNotFound() {
        //Arrange
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        //Act
        User result = userServiceImpl.getUserByUsername("username");
        //Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    void getUserById() {
        //Arrest
        when(userRepository.findById(1L)).thenReturn(Optional.of(this.user));
        //Act
        UserDto result = userServiceImpl.getUserById(1L);
        //Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createUserTestPassNoAuthenticatedException() {
        //Arrest
        this.userDto.setPassword("password");
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceImpl.createUser(this.userDto));
        //Asser
        assertEquals("Password must contain at least one uppercase letter, one digit, one special character, min 10 char and max 32 char", exception.getMessage());
    }

    @Test
    void createUserTestUsernameExistException() {
        //Arrest
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceImpl.createUser(this.userDto));
        //Asser
        assertEquals("Username already exists: " + userDto.getUsername(), exception.getMessage());
    }

    @Test
    void createUserTestEmailExistException() {
        //Arrest
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userServiceImpl.createUser(this.userDto));
        //Asser
        assertEquals("Email already exists: " + userDto.getEmail(), exception.getMessage());
    }

    @Test
    void createUserHashPasswordAuthentication() {
        // Arrange
        this.user.setPassword(passwordSecurity.hashPassword(this.userDto.getPassword()));
        when(userRepository.save(any(User.class))).thenReturn(this.user);

        // Act
        UserDto result = userServiceImpl.createUser(this.userDto);

        // Assert
        assertNotNull(result);
        assertNotEquals(this.userDto.getPassword(), result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }



    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}
package cz.syntaxbro.erpsystem.services.impl;


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

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceImpl = new UserServiceImpl(userRepository, roleRepository);
        this.user = new User(1L,"username", "1!Password", "firstName", "lastName", "email@email", true, Set.of());
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

    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}
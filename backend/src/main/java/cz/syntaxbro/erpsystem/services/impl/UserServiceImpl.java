package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.utils.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = getUserByIdOrThrow(id);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createUserRequest.getUsername());
        }
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createUserRequest.getEmail());
        }


        User user = mapToEntity(createUserRequest, new User());
        // Here we set the password - you can use the password from the DTO if available, or the default password
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        user.setActive(false);

        Set<Role> roles = createUserRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, CreateUserRequest userDto) {
        User user = getUserByIdOrThrow(id);
        mapToEntity(userDto, user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // Helper method to get the user by ID or throw an exception if the user does not exist.
    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    // Converts UserDto to User entity by setting attributes and resolving roles.
    private User mapToEntity(CreateUserRequest userDto, User user) {
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return user;
    }

}
package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.partials.UserPartial;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return getUserByIdOrThrow(id);
    }

    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createUserRequest.getUsername());
        }
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createUserRequest.getEmail());
        }

        User user = mapToEntity(createUserRequest, new User());
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserPartial userDto) {
        System.out.println("updateUser + " + userDto);
        var optionalUser = getUserByIdOrThrow(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setActive(userDto.isActive());
            user.setRoles(userDto.getRoles()
                    .stream()
                    .map(role -> roleRepository.findByName("ROLE_" + role.toUpperCase()))
                    .filter(Optional::isPresent)
                    .map(Optional::get).collect(Collectors.toSet()));

            return userRepository.save(user);
        }
        throw new IllegalArgumentException("User not found: " + id);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Helper method to get the user by ID or throw an exception if the user does not exist.
    private Optional<User> getUserByIdOrThrow(Long id) {
        return Optional.of(userRepository
                .findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id)
            )
        );
    }

    // Converts UserDto to User entity by setting attributes and resolving roles.
    private User mapToEntity(CreateUserRequest userDto, User user) {
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        return user;
    }
}
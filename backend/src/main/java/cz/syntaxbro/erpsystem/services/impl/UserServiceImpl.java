package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.exceptions.UserNotFoundException;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.utils.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordSecurity passwordSecurity;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordSecurity passwordSecurity) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordSecurity = passwordSecurity;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        return createUserInternal(request);
    }

    /**
     * Interní metoda pro vytvoření uživatele – umožňuje opětovné použití v `importUsersFromCsv`
     */
    private UserDto createUserInternal(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        if (!passwordSecurity.passwordValidator(request.getPassword())) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one digit, one special character, min 10 char and max 32 char");
        }

        User user = mapToEntity(request);
        user.setPassword(passwordSecurity.hashPassword(request.getPassword()));

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = getUserByIdOrThrow(id);
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

    /**
     * Pomocná metoda pro získání uživatele podle ID nebo vyvolání výjimky, pokud neexistuje.
     */
    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    /**
     * Převod `CreateUserRequest` na `User` entitu.
     */
    private User mapToEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setActive(request.isActive());

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return user;
    }

    @Override
    public void importUsersFromCsv(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",");

                // Kontrola správného formátu CSV
                if (data.length < 3) {
                    throw new IllegalArgumentException("Invalid CSV format at line " + lineCount);
                }

                CreateUserRequest request = new CreateUserRequest(
                        data[0].trim(),
                        data[2].trim(),
                        data[1].trim(),
                        "", "", true,
                        Set.of("ROLE_USER")
                );

                createUserInternal(request);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to import users from CSV file", e);
        }
    }
}
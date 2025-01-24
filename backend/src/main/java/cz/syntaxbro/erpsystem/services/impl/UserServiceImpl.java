// REVIEWED

package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.RoleRepository;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto, new User());
        user.setPassword(passwordEncoder.encode("defaultPassword")); // Set default password
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user = mapToEntity(userDto, user); // Map DTO to existing user entity
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }

    private User mapToEntity(UserDto userDto, User user) {
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setActive(userDto.isActive());

        Set<Role> roles = userDto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return user;
    }
}
package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordSecurity passwordSecurity;


    @Autowired
    public AuthServiceImpl(UserService userService, PasswordSecurity passwordSecurity) {
        this.userService = userService;
        this.passwordSecurity = passwordSecurity;
    }

    @Override
    public void registerUser(SignUpRequest signUpRequest) {
        if (userService.getUserByUsername(signUpRequest.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists: " + signUpRequest.getUsername());
        }

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(signUpRequest.getUsername());
        request.setEmail(signUpRequest.getEmail());
        request.setPassword(signUpRequest.getPassword());
        request.setRoles(Set.of("ROLE_USER")); // Default role

        userService.createUser(request);
    }

    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());
        if (user == null || !passwordSecurity.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("Invalid username or password");
        }
        return "generated-jwt-token";
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Get role names as Set<String>
        Set<String> currentRoleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Convert role names to Role objects
        Set<Role> roles = currentRoleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());

        // Create an instance of the User entity and set the necessary fields
        User currentUser = new User();
        currentUser.setId(1L);  // Sample value, normally obtained dynamically
        currentUser.setUsername(authentication.getName());
        currentUser.setFirstName("current_firstName");
        currentUser.setLastName("current_lastName");
        currentUser.setEmail("current_user@exampler.com");
        currentUser.setActive(true);
        currentUser.setRoles(roles);

        // Mapping the User entity to UserDto using UserMapper.toDto()
        return UserMapper.toDto(currentUser);
    }

}
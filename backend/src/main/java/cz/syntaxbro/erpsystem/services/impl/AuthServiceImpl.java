package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.LoginRequest;
import cz.syntaxbro.erpsystem.models.dtos.SignUpRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.utils.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public void registerUser(SignUpRequest signUpRequest) {
        List.of(
                Map.entry(signUpRequest.getUsername(), "Username"),
                Map.entry(signUpRequest.getEmail(), "Email"),
                Map.entry(signUpRequest.getPassword(), "Password")
        ).forEach(entry -> {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                throw new IllegalArgumentException(entry.getValue() + " cannot be null or empty");
            }
        });

        System.out.println("Checking if user exists: " + signUpRequest.getUsername());
        System.out.println("Saving user to database: " + signUpRequest.getUsername());
    }

    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        if ("validUserName".equals(loginRequest.getUsername()) &&
                "validPassword".equals(loginRequest.getPassword())) {
            return "generated-jwt-token";
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
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
        User currentUser = User.builder()
            .id(1L)
            .username(authentication.getName())
            .firstName("current_firstName")
            .lastName("current_lastName")
            .email("current_user@examler.com")
            .isActive(true)
            .roles(roles)
            .build();

        // Convert user entity to DTO via method.
        return currentUser.toDto();
    }
}
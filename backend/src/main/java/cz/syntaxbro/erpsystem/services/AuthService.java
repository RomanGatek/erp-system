package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.dtos.LoginRequest;
import cz.syntaxbro.erpsystem.models.dtos.SignUpRequest;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {


    public void registerUser(SignUpRequest signUpRequest) {
        if (signUpRequest.getUsername() == null || signUpRequest.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        System.out.println("Checking if user exists: " + signUpRequest.getUsername());
        System.out.println("Saving user to database: " + signUpRequest.getUsername());
    }

    public String authenticateUser(LoginRequest loginRequest) {
        if ("validUserName".equals(loginRequest.getUsername()) && "validPassword"
                .equals(loginRequest.getPassword())) {
            return "generated-jwt-token";
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public UserDto getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Set<String> currentRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean isActive = true;

        return new UserDto(
                1L,
                authentication.getName(),
                "current_firstName",
                "current_lastName",
                "current_user@exampler.com",
                isActive,
                currentRoles);
    }
}
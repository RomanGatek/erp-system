package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.utils.JwtUtil;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordSecurity security;
    private final UserService userService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordSecurity security, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.security = security;
        this.userService = userService;
    }

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
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!security.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return jwtUtil.generateToken(new CustomUserDetails(user));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Get role names as Set<String>
        Set<Role> currentRoleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::new)
                .collect(Collectors.toSet());

        // Create an instance of the User entity and set the necessary fields
        User currentUser = new User();
        Object principal = authentication.getPrincipal();
        User curentUser = new User();
        if (principal instanceof CustomUserDetails) {
            String username = ((CustomUserDetails) principal).getUsername();
            curentUser = userService.getUserByEmail(username);
        }
        // Mapping the User entity to UserDto using UserMapper.toDto()
        return currentUser;
    }
}
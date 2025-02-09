package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.configs.SecurityConfig;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.models.Role;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.services.UserService;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.utils.UserMapper;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;


    @Autowired
    public AuthServiceImpl(UserService userService) {
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
            }else{
                UserDto userDto = new UserDto();
                userDto.setUsername(signUpRequest.getUsername());
                userDto.setEmail(signUpRequest.getEmail());
                userDto.setPassword(signUpRequest.getPassword());
                userDto.setRoles(Set.of("ROLE_USER"));
                userService.createUser(userDto);
            }
        });
    }


    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username");
        }

        // authenticate with hash password algorithm SHA256!
        SecurityConfig security = new SecurityConfig();
        if (!security.hashPassword(loginRequest.getPassword()).equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
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
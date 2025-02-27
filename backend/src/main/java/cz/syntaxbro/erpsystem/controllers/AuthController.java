package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.configs.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.partials.UserPartial;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.PasswordChangeRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // User Registration
    @PostMapping("/public/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // User Login
    @PostMapping("/public/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(token);
    }

    // Fetching the current user
    @GetMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    // Fetching the current user
    @PutMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateCurrentUser(@Valid @RequestBody UserPartial user) {
        User currentUser = authService.getCurrentUser();
        currentUser.setUsername(user.getUsername());
        currentUser.setEmail(user.getEmail());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setActive(user.isActive());
        var savedUser = userRepository.save(currentUser);
        return ResponseEntity.ok(savedUser);
    }

    // Fetching the current user
    @PostMapping("/user/me/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> changePassword(@Valid @RequestBody PasswordChangeRequest passwordForm) {
        ErpSystemApplication.getLogger().info("Changing password: password: " + passwordForm);
        User currentUser = authService.getCurrentUser();
        PasswordSecurity security = new PasswordSecurity();
        currentUser.setPassword(security.encode(passwordForm.getPassword()));
        return ResponseEntity.ok(currentUser);
    }
}

package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.partials.UserPartial;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.PasswordChangeRequest;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import cz.syntaxbro.erpsystem.services.impl.FileStorageServiceImp;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final FileStorageServiceImp fileStorageService;

    public AuthController(AuthService authService, UserRepository userRepository, FileStorageServiceImp fileStorageService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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

    @PostMapping(value = "/user/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }

            User currentUser = authService.getCurrentUser();
            String fileName = fileStorageService.storeFile(file, currentUser.getEmail());
            
            // Update avatar URL in user profile
            currentUser.setAvatar("/uploads/" + fileName);
            User updatedUser = userRepository.save(currentUser);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Could not upload avatar: " + e.getMessage());
        }
    }
}

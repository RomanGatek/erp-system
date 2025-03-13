package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.partials.UserPartial;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.PasswordChangeRequest;
import cz.syntaxbro.erpsystem.responses.JWTResponse;
import cz.syntaxbro.erpsystem.security.PasswordSecurity;
import cz.syntaxbro.erpsystem.security.services.CustomUserDetails;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.services.impl.FileStorageServiceImp;
import cz.syntaxbro.erpsystem.utils.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/me")
@Validated
@EnableMethodSecurity(prePostEnabled = true)
public class MeController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final FileStorageServiceImp fileStorageService;
    private final Logger logger = ErpSystemApplication.getLogger();
    private final JwtUtil jwtUtil;

    @Autowired
    public MeController(AuthService authService, UserRepository userRepository, FileStorageServiceImp fileStorageService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    // Fetching the current user
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    // Fetching the current user
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateCurrentUser(@Valid @RequestBody UserPartial user) {
        User currentUser = authService.getCurrentUser();
        currentUser.map(user);
        return ResponseEntity.ok(userRepository.save(currentUser));
    }

    // Fetching the current user
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> changePassword(@Valid @RequestBody PasswordChangeRequest passwordForm) {
        User currentUser = authService.getCurrentUser();
        PasswordSecurity security = new PasswordSecurity();
        currentUser.setPassword(security.encode(passwordForm.getPassword()));
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

            logger.info("UPDATE me user - {} avatar: {}", updatedUser.getEmail(), updatedUser.getAvatar());

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Could not upload avatar: " + e.getMessage());
        }
    }

    @GetMapping("/renew")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JWTResponse> renew(@Valid @RequestParam("refreshToken") String refreshToken) {
        ErpSystemApplication.getLogger().info("Refresh token: {}", refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var isRefreshTokenValid = jwtUtil.validateToken(refreshToken, userDetails);
        var tokenIsExpired = jwtUtil.isTokenExpired(refreshToken);

        if (isRefreshTokenValid && !tokenIsExpired) {
            String token = authService.authenticateAccessToken(userDetails.getUser().getEmail());
            String newRefreshToken = authService.authenticateRefreshToken(userDetails.getUser().getEmail());
            return ResponseEntity.ok(new JWTResponse(token, newRefreshToken));
        }
        return ResponseEntity.status(401).build();
    }
}

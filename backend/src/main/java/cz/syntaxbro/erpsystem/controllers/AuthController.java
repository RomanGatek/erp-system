package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.exceptions.ErrorEntity;
import cz.syntaxbro.erpsystem.responses.JWTResponse;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // User Registration
    @PostMapping("/public/signup")
    public ResponseEntity<ErrorEntity> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorEntity("server", e.getMessage()));
        }
    }

    // User Login
    @PostMapping("/public/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticateUser(loginRequest);
        String refreshToken = authService.getRefreshToken(loginRequest);
        return ResponseEntity.status(200).body(new JWTResponse(token, refreshToken));
    }
}

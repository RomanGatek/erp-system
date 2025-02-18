package cz.syntaxbro.erpsystem.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordSecurity {

    private final PasswordEncoder passwordEncoder;

    public PasswordSecurity() {
        this.passwordEncoder = new BCryptPasswordEncoder();// Initialization inside the constructor
    }

    // Password hashing using BCrypt
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Verify password against stored hash
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    // Password validation (at least 1 uppercase, 1 number, 1 special char, 10-32 characters)
    public boolean passwordValidator(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,32}$";
        return Pattern.matches(passwordPattern, password);
    }
}
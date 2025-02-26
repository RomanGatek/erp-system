package cz.syntaxbro.erpsystem.configs;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

@Component
public class PasswordSecurity implements PasswordEncoder {

    @Override
    public String encode(CharSequence password) {
        try {
            String rawPassword = password.toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String receivedHash = this.encode(rawPassword.toString());
        return encodedPassword.equals(receivedHash);
    }

    // Password validation (at least 1 uppercase, 1 number, 1 special char, 10-32 characters)
    public boolean passwordValidator(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W+])[\\w\\W+]{10,32}$";
        return Pattern.matches(passwordPattern, password);
    }


}
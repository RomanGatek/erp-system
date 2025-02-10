package cz.syntaxbro.erpsystem.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (use with caution in production)
                .authorizeHttpRequests(auth -> auth
                        // API endpoint rules
                        .requestMatchers("/api/auth/public/**").permitAll() // Allow public API endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Secure API for ADMIN
                        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER") // Secure API for MANAGER
                        .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "MANAGER", "USER") // Secure API for users
                        .requestMatchers("/api/**").authenticated() // Default rule: all /api/** must be authenticated

                        // UI-based routes
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Web admin panel
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/user/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/guest/**").permitAll() // Public web pages
                        .anyRequest().authenticated() // Everything else requires authentication

                )
                .httpBasic(withDefaults()) //Basic Auth (for testing)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt encoder for secure password storage
    }

    //Hash password with algorithm SHA256!
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    //validate password mast have (1 capital char, 1 special char, 1 number, 10 char min and 32 char max)
    public boolean passwordValidator(String password){
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,32}$";
        return Pattern.matches(passwordPattern, password);
    }
}

package cz.syntaxbro.erpsystem.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (use with caution in production)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Access restricted to ADMIN role
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // Access for ADMIN and MANAGER
                        .requestMatchers("/user/**").hasAnyRole("ADMIN", "MANAGER", "USER") // Access for ADMIN, MANAGER, USER
                        .requestMatchers("/guest/**").permitAll() // Public access
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .permitAll() // Public access to login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL for logout
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .permitAll() // Public access to logout
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt encoder for secure password storage
    }
}
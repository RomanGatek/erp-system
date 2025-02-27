package cz.syntaxbro.erpsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable csrf for login, logout, signup
/*                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/auth/public/login",
                        "/api/auth/public/signup",
                        "/api/auth/public/logout"
                ))*/

                .cors(Customizer.withDefaults())

                .csrf(csfr -> csfr.disable())

                .authorizeHttpRequests(auth -> auth
                        // API endpoint rules
                        .requestMatchers("/api/auth/public/**").permitAll() // Allow public API endpoints
                        .requestMatchers("/api/csrf-token").permitAll() // Allow csrf-token endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Secure API for ADMIN
                        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER") // Secure API for MANAGER
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER", "USER") // Secure API for users
                        .requestMatchers("/api/**").authenticated() // Default rule: all /api/** must be authenticated
                        .anyRequest().authenticated() // Everything else requires authentication
                )

                // JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}

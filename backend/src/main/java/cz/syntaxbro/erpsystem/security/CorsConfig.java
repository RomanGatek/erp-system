package cz.syntaxbro.erpsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
        @Override   
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                  .allowedOriginPatterns("*") // Povolit všechny originy (včetně dynamických portů)
                  .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                  .allowedHeaders("*")
                  .exposedHeaders("Authorization")
                  .allowCredentials(true) // Funguje s allowedOriginPatterns("*")
                  .maxAge(3600L);

                registry.addMapping("/actuator/**")
                        .allowedOriginPatterns("*") // Povolit všechny originy (včetně dynamických portů)
                        .allowedMethods("GET", "HEAD")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true) // Funguje s allowedOriginPatterns("*")
                        .maxAge(3600L);
            }
        };
    }
}
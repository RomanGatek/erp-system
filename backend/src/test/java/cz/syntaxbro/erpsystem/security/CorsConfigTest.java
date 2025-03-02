package cz.syntaxbro.erpsystem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit test for the CorsConfig class.
 * Ensures that CORS settings are correctly applied.
 */
class CorsConfigTest {

    private WebMvcConfigurer webMvcConfigurer;

    @Mock
    private CorsRegistry corsRegistry;

    @Mock
    private CorsRegistration corsRegistration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CorsConfig corsConfig = new CorsConfig();
        webMvcConfigurer = corsConfig.corsConfigurer();

        // Mock method chaining properly to prevent NullPointerException
        when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(any())).thenReturn(corsRegistration);
        when(corsRegistration.exposedHeaders(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);
    }

    /**
     * Test: Ensures CORS settings are applied correctly.
     * Expected Result: API paths allow expected origins, methods, and headers.
     */
    @Test
    void shouldConfigureCorsCorrectly() {
        // Arrange: Argument captor to capture API path
        ArgumentCaptor<String> mappingCaptor = ArgumentCaptor.forClass(String.class);

        // Act: Apply CORS configuration
        webMvcConfigurer.addCorsMappings(corsRegistry);

        // Assert: Verify API path is correctly mapped
        verify(corsRegistry).addMapping(mappingCaptor.capture());

        // Ensure the correct mapping is applied
        assertThat(mappingCaptor.getValue()).isEqualTo("/api/**");

        // Verify that expected CORS settings are applied
        verify(corsRegistration).allowedOrigins("http://localhost:5173");
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(corsRegistration).allowedHeaders(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "*"
        );
        verify(corsRegistration).exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
        verify(corsRegistration).allowCredentials(true);
        verify(corsRegistration).maxAge(3600L);
    }
}
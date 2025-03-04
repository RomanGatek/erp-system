package cz.syntaxbro.erpsystem.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secretKey = "ThisIsASecretKeyForTestingOnly12345!"; // Ensure at least 32 bytes for HMAC SHA-256

    private UserDetails testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey); // Inject mock secret

        // Correct way to create a Spring Security UserDetails instance
        testUser = User.withUsername("testUser")
                .password("password")
                .roles("USER")
                .build();

        // Generate a valid token for testing
        validToken = generateValidToken();
    }

    private String generateValidToken() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10); // Token valid for 10 minutes

        return Jwts.builder()
                .subject(testUser.getUsername())
                .issuedAt(new Date())
                .expiration(calendar.getTime())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Test: Expired token should not be validated.
     * Expected result: Throws ExpiredJwtException.
     */
    @Test
    void shouldNotValidateExpiredToken() {
        // Arrange: Create an expired token
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -10); // Expired 10 seconds ago

        String expiredToken = Jwts.builder()
                .subject(testUser.getUsername())
                .issuedAt(new Date())
                .expiration(calendar.getTime())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        // Act & Assert: Expect ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(expiredToken, testUser));
    }

    /**
     * Test: Invalid token should not be validated.
     * Expected result: Throws SignatureException.
     */
    @Test
    void shouldNotValidateInvalidToken() {
        // Arrange: Modify valid token to make it invalid
        String invalidToken = validToken.substring(0, validToken.length() - 5) + "invalidPart";

        // Act & Assert: Expect SignatureException
        assertThrows(SignatureException.class, () -> jwtUtil.validateToken(invalidToken, testUser));
    }
}
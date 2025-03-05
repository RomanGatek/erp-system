package cz.syntaxbro.erpsystem.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWTResponseTest {

    /**
     * Tests the constructor with parameters.
     * Verifies that when creating an object using this constructor,
     * the `accessToken` and `refreshToken` values are correctly initialized.
     * Expected output:
     * - `assertEquals` should return success if the values match the ones passed to the constructor.
     * - If the values do not match, the test should fail.
     */
    @Test
    void testConstructorWithParameters() {
        // Creating an object using the constructor with parameters
        String accessToken = "access-token-example";
        String refreshToken = "refresh-token-example";
        JWTResponse jwtResponse = new JWTResponse(accessToken, refreshToken);

        // Verifying that the getters return correct values
        assertEquals(accessToken, jwtResponse.getAccessToken(), "Access token does not match");
        assertEquals(refreshToken, jwtResponse.getRefreshToken(), "Refresh token does not match");
    }

    /**
     * Tests the no-argument constructor and value setting using reflection.
     * Since Lombok does not generate setters in this class, values are set using reflection.
     * Verifies:
     * - That an object can be created using the no-argument constructor.
     * - That values can be correctly set and retrieved using getters.
     * Expected output:
     * - `assertEquals` should return success if the values are correctly set.
     * - If the values are not set correctly, or the getter returns an incorrect value, the test should fail.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        // Creating an object using the no-argument constructor
        JWTResponse jwtResponse = new JWTResponse();

        // New values to test
        String accessToken = "new-access-token";
        String refreshToken = "new-refresh-token";

        // Using reflection because Lombok does not generate setters automatically
        try {
            var accessTokenField = JWTResponse.class.getDeclaredField("accessToken");
            accessTokenField.setAccessible(true); // Allows access to the private field
            accessTokenField.set(jwtResponse, accessToken); // Sets a new value

            var refreshTokenField = JWTResponse.class.getDeclaredField("refreshToken");
            refreshTokenField.setAccessible(true); // Allows access to the private field
            refreshTokenField.set(jwtResponse, refreshToken); // Sets a new value

            // Verifying values using getters
            assertEquals(accessToken, jwtResponse.getAccessToken(), "Access token is not correctly set");
            assertEquals(refreshToken, jwtResponse.getRefreshToken(), "Refresh token is not correctly set");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Error accessing JWTResponse fields: " + e.getMessage());
        }
    }
}
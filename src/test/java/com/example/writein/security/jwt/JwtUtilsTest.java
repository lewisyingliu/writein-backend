package com.example.writein.security.jwt;

import com.example.writein.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "dGVzdC1vbmx5LWp3dC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhzNTEyLWFsZ29yaXRobS1yZXF1aXJlbWVudHM=");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    void generateJwtToken_returnsValidToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", Set.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserNameFromJwtToken_returnsCorrectUsername() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", Set.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_withValidToken_returnsTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", Set.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.here"));
    }

    @Test
    void validateJwtToken_withEmptyToken_returnsFalse() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtToken_withExpiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", Set.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertFalse(jwtUtils.validateJwtToken(token));
    }
}

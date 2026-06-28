package com.fahmak.alena.user.security;

import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set properties via Reflection since it's injected via @Value
        ReflectionTestUtils.setField(jwtService, "secret", "verysecretkeyverysecretkeyverysecretkey");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test@fahmak.com");
        user.setRole(Role.STUDENT);
        user.setFirstName("John");
        user.setLastName("Doe");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("test@fahmak.com", jwtService.extractEmail(token));
        assertEquals(10L, jwtService.extractUserId(token));
        assertEquals("STUDENT", jwtService.extractRole(token));
    }

    @Test
    void isTokenValid_WithEmail_ShouldReturnTrueForMatch() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test@fahmak.com");
        user.setRole(Role.STUDENT);

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, "test@fahmak.com"));
        assertFalse(jwtService.isTokenValid(token, "other@fahmak.com"));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.string"));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() {
        // Set expiration to 1 ms so it expires immediately
        ReflectionTestUtils.setField(jwtService, "expirationMs", 1L);
        User user = new User();
        user.setId(10L);
        user.setEmail("test@fahmak.com");
        user.setRole(Role.STUDENT);

        String token = jwtService.generateToken(user);

        // Add a small delay to ensure expiration
        try { Thread.sleep(10); } catch (InterruptedException e) {}

        assertFalse(jwtService.isTokenValid(token));
    }
}

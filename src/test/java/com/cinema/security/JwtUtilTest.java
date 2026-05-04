package com.cinema.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "cinema-secret-key-must-be-at-least-32-chars!!");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtil.generateToken("mahjoub@cinema.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtUtil.generateToken("mahjoub@cinema.com");

        String email = jwtUtil.extractEmail(token);

        assertEquals("mahjoub@cinema.com", email);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken("mahjoub@cinema.com");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = jwtUtil.generateToken("mahjoub@cinema.com");
        String tamperedToken = token + "tampered";

        assertFalse(jwtUtil.validateToken(tamperedToken));
    }
}
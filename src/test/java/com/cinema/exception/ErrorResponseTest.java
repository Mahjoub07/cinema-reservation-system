package com.cinema.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateWithThreeArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(404, "Not found", now);
        assertEquals(404, error.getStatus());
        assertEquals("Not found", error.getMessage());
        assertEquals(now, error.getTimestamp());
    }

    @Test
    void shouldCreateWithFourArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");
        ErrorResponse error = new ErrorResponse(400, "Bad request", now, errors);
        assertEquals(400, error.getStatus());
        assertEquals("Bad request", error.getMessage());
        assertEquals(now, error.getTimestamp());
        assertEquals(errors, error.getErrors());
    }

    @Test
    void shouldSetAndGetFields() {
        ErrorResponse error = new ErrorResponse(500, "Error", LocalDateTime.now());
        error.setStatus(503);
        error.setMessage("Service unavailable");
        error.setTimestamp(LocalDateTime.of(2024, 1, 1, 0, 0));
        Map<String, String> errors = new HashMap<>();
        error.setErrors(errors);

        assertEquals(503, error.getStatus());
        assertEquals("Service unavailable", error.getMessage());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), error.getTimestamp());
        assertEquals(errors, error.getErrors());
    }
}

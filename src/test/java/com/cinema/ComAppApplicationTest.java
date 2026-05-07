package com.cinema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ComAppApplicationTest {

    @Test
    void shouldInstantiate() {
        assertDoesNotThrow(() -> new ComAppApplication());
    }
}

package com.cinema.websocket;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SeatEventTest {

    @Test
    void shouldCreateSeatEventWithDefaultConstructor() {
        SeatEvent event = new SeatEvent();
        assertNull(event.getMovieId());
        assertNull(event.getShowTime());
        assertNull(event.getSeatNumber());
        assertNull(event.getAction());
        assertNull(event.getSessionId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void shouldCreateSeatEventWithAllArgsConstructor() {
        SeatEvent event = new SeatEvent(1L, "2024-01-01T10:00", "A1", SeatEvent.Action.LOCKED, "session123");
        assertEquals(1L, event.getMovieId());
        assertEquals("2024-01-01T10:00", event.getShowTime());
        assertEquals("A1", event.getSeatNumber());
        assertEquals(SeatEvent.Action.LOCKED, event.getAction());
        assertEquals("session123", event.getSessionId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void shouldSetAndGetFields() {
        SeatEvent event = new SeatEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setMovieId(2L);
        event.setShowTime("2024-01-01T12:00");
        event.setSeatNumber("B2");
        event.setAction(SeatEvent.Action.RELEASED);
        event.setSessionId("session456");
        event.setTimestamp(now);

        assertEquals(2L, event.getMovieId());
        assertEquals("2024-01-01T12:00", event.getShowTime());
        assertEquals("B2", event.getSeatNumber());
        assertEquals(SeatEvent.Action.RELEASED, event.getAction());
        assertEquals("session456", event.getSessionId());
        assertEquals(now, event.getTimestamp());
    }

    @Test
    void shouldContainAllActionValues() {
        assertNotNull(SeatEvent.Action.SELECTED);
        assertNotNull(SeatEvent.Action.RELEASED);
        assertNotNull(SeatEvent.Action.LOCKED);
        assertNotNull(SeatEvent.Action.BOOKED);
        assertNotNull(SeatEvent.Action.TIMEOUT);
        assertEquals(5, SeatEvent.Action.values().length);
    }
}

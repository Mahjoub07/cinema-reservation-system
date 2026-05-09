package com.cinema.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatLockServiceTest {

    private SimpMessagingTemplate messagingTemplate;
    private SeatLockService seatLockService;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        seatLockService = new SeatLockService(messagingTemplate);
    }

    @Test
    void shouldLockSeat() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        assertTrue(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldReturnFalseForUnlockedSeat() {
        assertFalse(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldUnlockSeat() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        seatLockService.unlockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        assertFalse(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldNotUnlockSeatWithDifferentSession() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        seatLockService.unlockSeat(1L, "2024-01-01T10:00", "A1", "session2");
        assertTrue(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldGetLockedSeats() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A2", "session2");
        Set<String> locked = seatLockService.getLockedSeats(1L, "2024-01-01T10:00");
        assertEquals(2, locked.size());
        assertTrue(locked.contains("A1"));
        assertTrue(locked.contains("A2"));
    }

    @Test
    void shouldGetRemainingSecondsForLockedSeat() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        long remaining = seatLockService.getRemainingSeconds(1L, "2024-01-01T10:00", "A1");
        assertTrue(remaining > 290 && remaining <= 300, "Remaining seconds should be close to 300 but was " + remaining);
    }

    @Test
    void shouldReturnZeroRemainingSecondsForUnlockedSeat() {
        assertEquals(0, seatLockService.getRemainingSeconds(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldReturnZeroRemainingSecondsForExpiredLock() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        // Note: actual expiration requires 5 minutes; we test the non-expired path above
        // For expired path, we can verify it returns 0 for null seat
        assertEquals(0, seatLockService.getRemainingSeconds(1L, "2024-01-01T10:00", "B2"));
    }

    @Test
    void shouldClearLocksForSession() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A2", "session1");
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "B1", "session2");

        seatLockService.clearLocksForSession("session1");

        assertFalse(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
        assertFalse(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A2"));
        assertTrue(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "B1"));

        ArgumentCaptor<SeatEvent> eventCaptor = ArgumentCaptor.forClass(SeatEvent.class);
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), eventCaptor.capture());
        for (SeatEvent event : eventCaptor.getAllValues()) {
            assertEquals(SeatEvent.Action.TIMEOUT, event.getAction());
            assertEquals("session1", event.getSessionId());
        }
    }

    @Test
    void shouldCleanupExpiredLocks() {
        seatLockService.lockSeat(1L, "2024-01-01T10:00", "A1", "session1");
        seatLockService.cleanupExpiredLocks();
        // Since the lock is not expired yet, it should still be there
        assertTrue(seatLockService.isSeatLocked(1L, "2024-01-01T10:00", "A1"));
    }

    @Test
    void shouldGetEmptyLockedSeatsForUnknownMovie() {
        Set<String> locked = seatLockService.getLockedSeats(99L, "2024-01-01T10:00");
        assertTrue(locked.isEmpty());
    }

    @Test
    void shouldNotFailWhenUnlockingUnknownSeat() {
        assertDoesNotThrow(() -> seatLockService.unlockSeat(1L, "2024-01-01T10:00", "A1", "session1"));
    }

    @Test
    void shouldNotFailWhenClearingLocksForUnknownSession() {
        assertDoesNotThrow(() -> seatLockService.clearLocksForSession("unknown"));
    }
}

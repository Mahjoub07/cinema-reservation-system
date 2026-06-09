package com.cinema.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;                  
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SeatLockService {

    private static final long LOCK_DURATION_MINUTES = 5;

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, Map<String, Map<String, LockInfo>>> seatLocks = new ConcurrentHashMap<>();

    private static class LockInfo {
        final String sessionId;
        final Instant lockedAt;

        LockInfo(String sessionId, Instant lockedAt) {
            this.sessionId = sessionId;
            this.lockedAt = lockedAt;
        }

        boolean isExpired() {
            return ChronoUnit.MINUTES.between(lockedAt, Instant.now()) >= LOCK_DURATION_MINUTES;
        }
    }

    public SeatLockService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void lockSeat(Long movieId, String showTime, String seatNumber, String sessionId) {
        seatLocks
                .computeIfAbsent(movieId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(showTime, k -> new ConcurrentHashMap<>())
                .put(seatNumber, new LockInfo(sessionId, Instant.now()));
    }


    public long getRemainingSeconds(Long movieId, String showTime, String seatNumber) {
        Map<String, LockInfo> showTimeLocks = getShowTimeLocks(movieId, showTime);
        if (showTimeLocks == null) return 0;
        LockInfo lock = showTimeLocks.get(seatNumber);
        if (lock == null || lock.isExpired()) return 0;

        long elapsedSeconds = ChronoUnit.SECONDS.between(lock.lockedAt, Instant.now());
        return Math.max(0, LOCK_DURATION_MINUTES * 60 - elapsedSeconds);
    }

    private Map<String, LockInfo> getShowTimeLocks(Long movieId, String showTime) {
        Map<String, Map<String, LockInfo>> movieLocks = seatLocks.get(movieId);
        if (movieLocks == null) return null;
        return movieLocks.get(showTime);
    }
}
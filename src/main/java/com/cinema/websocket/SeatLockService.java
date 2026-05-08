package com.cinema.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SeatLockService {

    private static final long LOCK_DURATION_MINUTES = 5;

    private final SimpMessagingTemplate messagingTemplate;

    // movieId -> showTime -> seatNumber -> LockInfo
    private final Map<Long, Map<String, Map<String, LockInfo>>> seatLocks = new ConcurrentHashMap<>();

    private static class LockInfo {
        final String sessionId;
        final LocalDateTime lockedAt;

        LockInfo(String sessionId, LocalDateTime lockedAt) {
            this.sessionId = sessionId;
            this.lockedAt = lockedAt;
        }

        boolean isExpired() {
            return ChronoUnit.MINUTES.between(lockedAt, LocalDateTime.now()) >= LOCK_DURATION_MINUTES;
        }
    }

    public SeatLockService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void lockSeat(Long movieId, String showTime, String seatNumber, String sessionId) {
        seatLocks
                .computeIfAbsent(movieId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(showTime, k -> new ConcurrentHashMap<>())
                .put(seatNumber, new LockInfo(sessionId, LocalDateTime.now()));
    }

    public void unlockSeat(Long movieId, String showTime, String seatNumber, String sessionId) {
        Map<String, LockInfo> showTimeLocks = getShowTimeLocks(movieId, showTime);
        if (showTimeLocks != null) {
            showTimeLocks.computeIfPresent(seatNumber, (key, lock) ->
                lock.sessionId.equals(sessionId) ? null : lock
            );
        }
    }

    public boolean isSeatLocked(Long movieId, String showTime, String seatNumber) {
        Map<String, LockInfo> showTimeLocks = getShowTimeLocks(movieId, showTime);
        if (showTimeLocks == null) return false;
        LockInfo lock = showTimeLocks.get(seatNumber);
        if (lock == null) return false;
        return !lock.isExpired();
    }

    public void clearLocksForSession(String sessionId) {
        for (Map.Entry<Long, Map<String, Map<String, LockInfo>>> movieEntry : seatLocks.entrySet()) {
            for (Map.Entry<String, Map<String, LockInfo>> showEntry : movieEntry.getValue().entrySet()) {
                showEntry.getValue().entrySet().removeIf(entry -> {
                    if (entry.getValue().sessionId.equals(sessionId)) {
                        // Notify others that seat was released due to disconnect
                        SeatEvent timeoutEvent = new SeatEvent(
                                movieEntry.getKey(), showEntry.getKey(), entry.getKey(),
                                SeatEvent.Action.TIMEOUT, sessionId
                        );
                        messagingTemplate.convertAndSend(
                                "/topic/seats/" + movieEntry.getKey() + "/" + showEntry.getKey(),
                                timeoutEvent
                        );
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void cleanupExpiredLocks() {
        for (Map.Entry<Long, Map<String, Map<String, LockInfo>>> movieEntry : seatLocks.entrySet()) {
            for (Map.Entry<String, Map<String, LockInfo>> showEntry : movieEntry.getValue().entrySet()) {
                showEntry.getValue().entrySet().removeIf(entry -> {
                    if (entry.getValue().isExpired()) {
                        SeatEvent timeoutEvent = new SeatEvent(
                                movieEntry.getKey(), showEntry.getKey(), entry.getKey(),
                                SeatEvent.Action.TIMEOUT, entry.getValue().sessionId
                        );
                        messagingTemplate.convertAndSend(
                                "/topic/seats/" + movieEntry.getKey() + "/" + showEntry.getKey(),
                                timeoutEvent
                        );
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    public Set<String> getLockedSeats(Long movieId, String showTime) {
        Map<String, LockInfo> showTimeLocks = getShowTimeLocks(movieId, showTime);
        if (showTimeLocks == null) return new CopyOnWriteArraySet<>();

        Set<String> locked = new CopyOnWriteArraySet<>();
        for (Map.Entry<String, LockInfo> entry : showTimeLocks.entrySet()) {
            if (!entry.getValue().isExpired()) {
                locked.add(entry.getKey());
            }
        }
        return locked;
    }

    public long getRemainingSeconds(Long movieId, String showTime, String seatNumber) {
        Map<String, LockInfo> showTimeLocks = getShowTimeLocks(movieId, showTime);
        if (showTimeLocks == null) return 0;
        LockInfo lock = showTimeLocks.get(seatNumber);
        if (lock == null || lock.isExpired()) return 0;

        long elapsedSeconds = ChronoUnit.SECONDS.between(lock.lockedAt, LocalDateTime.now());
        return Math.max(0, LOCK_DURATION_MINUTES * 60 - elapsedSeconds);
    }

    private Map<String, LockInfo> getShowTimeLocks(Long movieId, String showTime) {
        Map<String, Map<String, LockInfo>> movieLocks = seatLocks.get(movieId);
        if (movieLocks == null) return null;
        return movieLocks.get(showTime);
    }
}

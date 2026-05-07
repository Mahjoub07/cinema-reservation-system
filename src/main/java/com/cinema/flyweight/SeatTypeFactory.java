package com.cinema.flyweight;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight Pattern: Factory.
 * Manages a pool of shared SeatType instances.
 * Ensures that only one instance of each seat type exists in memory,
 * reducing memory footprint when many seats reference the same type.
 */
@Component
public class SeatTypeFactory {

    private final Map<String, SeatType> pool = new ConcurrentHashMap<>();

    /**
     * Returns a shared SeatType instance for the given type key.
     * Creates the instance only if it does not already exist in the pool.
     *
     * @param type "STANDARD", "VIP", or "PREMIUM" (case-insensitive)
     * @return the shared SeatType flyweight
     */
    public SeatType getSeatType(String type) {
        String key = type.toUpperCase();
        return pool.computeIfAbsent(key, k -> {
            return switch (k) {
                case "VIP" -> SeatType.createVip();
                case "PREMIUM" -> SeatType.createPremium();
                default -> SeatType.createStandard();
            };
        });
    }

    /**
     * Returns the number of shared types currently in the pool.
     */
    public int getPoolSize() {
        return pool.size();
    }
}

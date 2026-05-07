package com.cinema.pricing;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy Pattern: Context class that holds a reference to a PricingStrategy.
 * Allows dynamic switching of pricing strategies at runtime.
 * Injected into BookingService to make pricing pluggable.
 */
@Component
public class PricingContext {

    private final Map<String, PricingStrategy> strategies;
    private PricingStrategy currentStrategy;

    public PricingContext(List<PricingStrategy> strategyList) {
        this.strategies = new HashMap<>();
        for (PricingStrategy strategy : strategyList) {
            this.strategies.put(strategy.getClass().getSimpleName(), strategy);
        }
        // Default to standard pricing for backward compatibility
        this.currentStrategy = this.strategies.getOrDefault(
                "StandardPricingStrategy",
                strategyList.isEmpty() ? null : strategyList.get(0)
        );
    }

    /**
     * Dynamically switch the active pricing strategy by class name.
     *
     * @param strategyName simple class name of the strategy (e.g., "StudentDiscountStrategy")
     */
    public void setStrategy(String strategyName) {
        PricingStrategy strategy = strategies.get(strategyName);
        if (strategy != null) {
            this.currentStrategy = strategy;
        }
    }

    /**
     * Calculates price using the currently active strategy.
     */
    public double calculatePrice(double basePrice, int seats) {
        if (currentStrategy == null) {
            return basePrice * seats;
        }
        return currentStrategy.calculatePrice(basePrice, seats);
    }

    /**
     * Returns names of all available pricing strategies.
     */
    public List<String> getAvailableStrategies() {
        return List.copyOf(strategies.keySet());
    }

    /**
     * Returns the currently active strategy name.
     */
    public String getCurrentStrategyName() {
        return currentStrategy != null ? currentStrategy.getClass().getSimpleName() : "None";
    }
}

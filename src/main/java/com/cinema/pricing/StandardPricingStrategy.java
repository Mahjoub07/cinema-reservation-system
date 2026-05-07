package com.cinema.pricing;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Standard pricing with no discounts.
 * Default strategy to maintain backward compatibility.
 */
@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice, int seats) {
        return basePrice * seats;
    }
}

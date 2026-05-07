package com.cinema.pricing;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Group discount pricing.
 * Applies a 15% discount when booking 5 or more seats.
 */
@Component
public class GroupDiscountStrategy implements PricingStrategy {

    private static final int GROUP_MIN_SEATS = 5;
    private static final double DISCOUNT_RATE = 0.15;

    @Override
    public double calculatePrice(double basePrice, int seats) {
        if (seats >= GROUP_MIN_SEATS) {
            return basePrice * seats * (1 - DISCOUNT_RATE);
        }
        return basePrice * seats;
    }
}

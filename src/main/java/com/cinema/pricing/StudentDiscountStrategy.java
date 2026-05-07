package com.cinema.pricing;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Student discount pricing.
 * Applies a 20% discount for students.
 */
@Component
public class StudentDiscountStrategy implements PricingStrategy {

    private static final double DISCOUNT_RATE = 0.20;

    @Override
    public double calculatePrice(double basePrice, int seats) {
        return basePrice * seats * (1 - DISCOUNT_RATE);
    }
}

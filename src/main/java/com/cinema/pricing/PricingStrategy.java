package com.cinema.pricing;

/**
 * Strategy Pattern: Interface for different pricing algorithms.
 * Allows dynamic selection of pricing behavior at runtime.
 */
public interface PricingStrategy {

    /**
     * Calculates the total price based on base price per seat and number of seats.
     *
     * @param basePrice price per seat
     * @param seats     number of seats
     * @return total calculated price
     */
    double calculatePrice(double basePrice, int seats);
}

package com.cinema.decorator;

import com.cinema.dto.MovieDTO;

/**
 * Decorator Pattern: Concrete Decorator.
 * Adds visual badges (e.g., SELLING FAST, BARGAIN) to movie descriptions
 * without modifying the base MovieService or original DTOs.
 */
public class BadgeDecorator implements MovieDescriptor {

    private final MovieDescriptor wrapped;

    public BadgeDecorator(MovieDescriptor wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String describe(MovieDTO movie) {
        String base = wrapped.describe(movie);
        String badge = determineBadge(movie);
        return base + " [" + badge + "]";
    }

    private String determineBadge(MovieDTO movie) {
        if (movie.getPrice() != null && movie.getPrice() < 5.0) {
            return "BARGAIN";
        }
        if (movie.getAvailableSeats() != null && movie.getAvailableSeats() <= 5) {
            return "SELLING FAST";
        }
        if (movie.getAvailableSeats() != null && movie.getAvailableSeats() > 50) {
            return "PLENTY OF SEATS";
        }
        return "NOW SHOWING";
    }
}

package com.cinema.decorator;

import com.cinema.dto.MovieDTO;

/**
 * Decorator Pattern: Concrete Decorator.
 * Appends a deterministic mock rating to the movie description
 * based on the movie title hash.
 */
public class RatingDecorator implements MovieDescriptor {

    private final MovieDescriptor wrapped;

    public RatingDecorator(MovieDescriptor wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String describe(MovieDTO movie) {
        String base = wrapped.describe(movie);
        double rating = calculateMockRating(movie);
        return base + String.format(" | Rating: %.1f/5.0", rating);
    }

    private double calculateMockRating(MovieDTO movie) {
        int hash = movie.getTitle() != null ? movie.getTitle().hashCode() : 0;
        return 3.0 + (Math.abs(hash) % 20) / 10.0;
    }
}

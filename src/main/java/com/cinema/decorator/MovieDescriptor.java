package com.cinema.decorator;

import com.cinema.dto.MovieDTO;

/**
 * Decorator Pattern: Component interface.
 * Defines the contract for generating movie descriptions.
 */
public interface MovieDescriptor {

    /**
     * Generates a description string for the given movie.
     *
     * @param movie the movie DTO
     * @return enhanced description
     */
    String describe(MovieDTO movie);
}

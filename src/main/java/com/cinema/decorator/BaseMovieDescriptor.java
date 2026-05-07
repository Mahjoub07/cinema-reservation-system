package com.cinema.decorator;

import com.cinema.dto.MovieDTO;
import org.springframework.stereotype.Component;

/**
 * Decorator Pattern: Concrete Component.
 * Provides the base movie description without any enhancements.
 */
@Component
public class BaseMovieDescriptor implements MovieDescriptor {

    @Override
    public String describe(MovieDTO movie) {
        return String.format("%s | Genre: %s | %s",
                movie.getTitle(),
                movie.getGenre(),
                movie.getDescription() != null ? movie.getDescription() : "No description");
    }
}

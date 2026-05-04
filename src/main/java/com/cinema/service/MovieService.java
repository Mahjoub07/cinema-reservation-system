package com.cinema.service;

import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public List<Movie> searchByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public Movie updateMovie(Long id, Movie updated) {
        Movie movie = getMovieById(id);
        movie.setTitle(updated.getTitle());
        movie.setDescription(updated.getDescription());
        movie.setGenre(updated.getGenre());
        movie.setDuration(updated.getDuration());
        movie.setShowTime(updated.getShowTime());
        movie.setAvailableSeats(updated.getAvailableSeats());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
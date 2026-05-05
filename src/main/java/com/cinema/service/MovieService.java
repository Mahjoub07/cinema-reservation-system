package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDTO addMovie(MovieDTO movieDTO) {
        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    public MovieDTO getMovieDTOById(Long id) {
        return convertToDTO(getMovieById(id));
    }

    public List<MovieDTO> searchByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        Movie movie = getMovieById(id);
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setGenre(movieDTO.getGenre());
        movie.setDuration(movieDTO.getDuration());
        movie.setShowTime(movieDTO.getShowTime());
        movie.setAvailableSeats(movieDTO.getAvailableSeats());
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getGenre(),
            movie.getDuration(),
            movie.getShowTime(),
            movie.getAvailableSeats(),
            null
        );
    }

    private Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDuration(dto.getDuration());
        movie.setShowTime(dto.getShowTime());
        movie.setAvailableSeats(dto.getAvailableSeats());
        return movie;
    }
}